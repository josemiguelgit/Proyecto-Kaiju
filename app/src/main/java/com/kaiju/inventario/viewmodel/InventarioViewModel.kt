
package com.kaiju.inventario.viewmodel

import androidx.lifecycle.ViewModel
import com.kaiju.inventario.model.FormularioProductoEstado
import com.kaiju.inventario.model.Producto
import com.kaiju.inventario.repository.InventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.kaiju.inventario.model.FormularioMovimientoEstado
import com.kaiju.inventario.model.SentidoAjuste
import com.kaiju.inventario.model.TipoMovimiento
import com.kaiju.inventario.model.Usuario
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


    class InventarioViewModel(
        private val repository: InventarioRepository
    ) : ViewModel() {

        private var guardandoProducto = false
        private var guardandoMovimiento = false

        val productos = repository.productos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        val movimientos = repository.movimientos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        init {
            viewModelScope.launch {
                repository.inicializarDatos()
            }
        }


        val categorias = listOf(
        "Poleras", "Pantalones", "Polerones",
        "Chaquetas", "Camisas", "Vestidos",
        "Shorts", "Faldas"
    )

    private val _formulario = MutableStateFlow(
        FormularioProductoEstado()
    )

    val formulario = _formulario.asStateFlow()

    private var codigoEnEdicion: String? = null

    fun prepararCreacion() {
        codigoEnEdicion = null
        _formulario.value = FormularioProductoEstado()
    }


        fun prepararEdicion(codigo: String) {
            codigoEnEdicion = codigo
            _formulario.value = FormularioProductoEstado()

            viewModelScope.launch {
                val producto = repository.buscarProducto(codigo)
                    ?: return@launch

                _formulario.value = FormularioProductoEstado(
                    codigo = producto.codigo,
                    nombre = producto.nombre,
                    descripcion = producto.descripcion,
                    categoria = producto.categoria,
                    tipo = producto.tipo,
                    precio = producto.precio.toString(),
                    stock = producto.stockActual.toString(),
                    stockMinimo = producto.stockMinimo.toString(),
                    detalle = producto.detalle.orEmpty()
                )
            }
        }


        fun actualizarCampo(campo: String, valor: String) {
        val actual = _formulario.value

        val nuevo = when (campo) {
            "codigo" -> {
                if (codigoEnEdicion != null) actual
                else actual.copy(codigo = valor)
            }
            "nombre" -> actual.copy(nombre = valor)
            "descripcion" -> actual.copy(descripcion = valor)
            "categoria" -> actual.copy(categoria = valor)
            "tipo" -> actual.copy(tipo = valor)
            "precio" -> actual.copy(precio = valor)
            "stock" -> {
                if (codigoEnEdicion != null) actual
                else actual.copy(stock = valor)
            }
            "stockMinimo" -> actual.copy(stockMinimo = valor)
            "detalle" -> actual.copy(detalle = valor)
            else -> actual
        }

        _formulario.value = nuevo.copy(
            errores = nuevo.errores - campo
        )
    }

    fun validarFormulario(): Boolean {
        val datos = _formulario.value
        val errores = mutableMapOf<String, String>()

        if (datos.codigo.isBlank()) {
            errores["codigo"] = "El código es obligatorio"
        } else if (
            codigoEnEdicion == null &&
            productos.value.any {
                it.codigo.equals(
                    datos.codigo.trim(),
                    ignoreCase = true
                )
            }
        ) {
            errores["codigo"] = "El código ya existe"
        }

        if (datos.nombre.trim().length < 3) {
            errores["nombre"] =
                "El nombre debe tener al menos 3 caracteres"
        }

        if (datos.categoria !in categorias) {
            errores["categoria"] = "Seleccione una categoría válida"
        }

        if (datos.tipo.isBlank()) {
            errores["tipo"] = "Ingrese el tipo de prenda"
        }

        val precio = datos.precio.toIntOrNull()
        if (precio == null || precio <= 0) {
            errores["precio"] = "Ingrese un precio entero mayor que 0"
        }

        val stock = datos.stock.toIntOrNull()
        if (stock == null || stock < 0) {
            errores["stock"] = "El stock no puede ser negativo"
        }

        val minimo = datos.stockMinimo.toIntOrNull()
        if (minimo == null || minimo < 0) {
            errores["stockMinimo"] =
                "El stock mínimo debe ser un entero no negativo"
        }

        _formulario.value = datos.copy(errores = errores)

        return errores.isEmpty()
    }

        fun guardarProducto(onResultado: (Boolean) -> Unit) {

            // Validar el formulario antes de guardar
            if (!validarFormulario()) {
                onResultado(false)
                return
            }

            // Evitar guardados simultáneos
            if (guardandoProducto) return
            guardandoProducto = true

            val datos = _formulario.value
            val codigoEdicionActual = codigoEnEdicion

            viewModelScope.launch {
                try {
                    // Buscar producto si estamos editando
                    val anterior = codigoEdicionActual?.let {
                        repository.buscarProducto(it)
                    }

                    if (codigoEdicionActual != null && anterior == null) {
                        _formulario.value = datos.copy(
                            errores = mapOf(
                                "general" to "Producto no encontrado"
                            )
                        )

                        onResultado(false)
                        return@launch
                    }

                    // Construir producto con los datos validados
                    val nuevoProducto = Producto(
                        codigo = anterior?.codigo ?: datos.codigo.trim(),
                        nombre = datos.nombre.trim(),
                        descripcion = datos.descripcion.trim(),
                        categoria = datos.categoria,
                        tipo = datos.tipo.trim(),
                        precio = datos.precio.toInt(),
                        stockActual = anterior?.stockActual
                            ?: datos.stock.toInt(),
                        stockMinimo = datos.stockMinimo.toInt(),
                        detalle = datos.detalle.trim().ifBlank { null }
                    )

                    // Crear o editar mediante Room
                    val resultado = if (anterior != null) {
                        repository.editarProducto(nuevoProducto)
                    } else {
                        repository.agregarProducto(nuevoProducto)
                    }

                    if (resultado) {
                        prepararCreacion()
                    } else {
                        _formulario.value = datos.copy(
                            errores = mapOf(
                                "general" to "No se pudo guardar el producto"
                            )
                        )
                    }

                    onResultado(resultado)

                } catch (e: kotlinx.coroutines.CancellationException) {
                    throw e

                } catch (e: Exception) {
                    _formulario.value = datos.copy(
                        errores = mapOf(
                            "general" to "Error inesperado al guardar el producto"
                        )
                    )
                    onResultado(false)

                } finally {
                    // Permitir futuros guardados
                    guardandoProducto = false
                }
            }
        }



        fun limpiarFormulario() {
        prepararCreacion()
    }


    private val _formularioMovimiento = MutableStateFlow(
        FormularioMovimientoEstado()
    )

    val formularioMovimiento = _formularioMovimiento.asStateFlow()

    fun actualizarCampoMovimiento(campo: String, valor: String) {
        val actual = _formularioMovimiento.value

        val nuevo = when (campo) {
            "codigoProducto" -> actual.copy(codigoProducto = valor)
            "tipo" -> actual.copy(
                tipo = valor,
                sentidoAjuste = "",
                motivo = ""
            )
            "cantidad" -> actual.copy(cantidad = valor)
            "sentidoAjuste" -> actual.copy(sentidoAjuste = valor)
            "motivo" -> actual.copy(motivo = valor)
            else -> actual
        }

        _formularioMovimiento.value = nuevo.copy(
            errores = emptyMap()
        )
    }

    fun validarFormularioMovimiento(): Boolean {

        val datos = _formularioMovimiento.value
        val errores = mutableMapOf<String, String>()

        val producto = productos.value.find {
            it.codigo.equals(
                datos.codigoProducto,
                ignoreCase = true
            )
        }

        if (producto == null) {
            errores["codigoProducto"] = "Seleccione un producto"
        }

        val tipo = TipoMovimiento.entries.firstOrNull {
            it.name == datos.tipo
        }

        if (tipo == null) {
            errores["tipo"] = "Seleccione un tipo de movimiento"
        }

        val cantidad = datos.cantidad.toIntOrNull()

        if (cantidad == null || cantidad <= 0) {
            errores["cantidad"] =
                "Ingrese una cantidad entera mayor que cero"
        }

        val sentido = SentidoAjuste.entries.firstOrNull {
            it.name == datos.sentidoAjuste
        }

        if (tipo == TipoMovimiento.AJUSTE) {
            if (sentido == null) {
                errores["sentidoAjuste"] =
                    "Seleccione aumentar o disminuir"
            }

            if (datos.motivo.isBlank()) {
                errores["motivo"] =
                    "El motivo del ajuste es obligatorio"
            }
        }

        val esSalida = tipo == TipoMovimiento.SALIDA_VENTA ||
                tipo == TipoMovimiento.SALIDA_CONSUMO ||
                (
                        tipo == TipoMovimiento.AJUSTE &&
                                sentido == SentidoAjuste.DISMINUCION
                        )

        if (producto != null && cantidad != null && esSalida) {
            if (cantidad > producto.stockActual) {
                errores["cantidad"] =
                    "Stock insuficiente. Disponible: ${producto.stockActual}"
            }
        }

        _formularioMovimiento.value = datos.copy(
            errores = errores
        )

        return errores.isEmpty()
    }



        fun guardarMovimiento(
            usuario: Usuario?,
            onResultado: (Boolean) -> Unit
        ) {

            // Evitar registrar el mismo movimiento varias veces
            if (guardandoMovimiento) return

            // Validar formulario
            if (!validarFormularioMovimiento()) {
                onResultado(false)
                return
            }

            // Verificar sesión activa
            if (usuario == null) {
                _formularioMovimiento.value =
                    _formularioMovimiento.value.copy(
                        errores = mapOf(
                            "general" to "Debe iniciar sesión"
                        )
                    )

                onResultado(false)
                return
            }

            guardandoMovimiento = true

            val datos = _formularioMovimiento.value

            val tipo = TipoMovimiento.valueOf(datos.tipo)

            val sentido = if (tipo == TipoMovimiento.AJUSTE) {
                SentidoAjuste.valueOf(datos.sentidoAjuste)
            } else {
                null
            }

            viewModelScope.launch {
                try {
                    // Registrar movimiento mediante Room
                    val error = repository.registrarMovimiento(
                        codigoProducto = datos.codigoProducto,
                        tipo = tipo,
                        cantidad = datos.cantidad.toInt(),
                        motivo = datos.motivo,
                        sentidoAjuste = sentido,
                        usuario = usuario
                    )

                    if (error == null) {
                        // Registro exitoso
                        limpiarFormularioMovimiento()
                        onResultado(true)

                    } else {
                        // Error de validación o regla de negocio
                        _formularioMovimiento.value = datos.copy(
                            errores = mapOf(
                                "general" to error
                            )
                        )

                        onResultado(false)
                    }

                } catch (e: kotlinx.coroutines.CancellationException) {
                    throw e

                } catch (e: Exception) {
                    _formularioMovimiento.value = datos.copy(
                        errores = mapOf(
                            "general" to "Error inesperado al registrar el movimiento"
                        )
                    )

                    onResultado(false)

                } finally {
                    // Permitir registrar otro movimiento
                    guardandoMovimiento = false
                }
            }
        }



        fun limpiarFormularioMovimiento() {
        _formularioMovimiento.value = FormularioMovimientoEstado()
    }


}
