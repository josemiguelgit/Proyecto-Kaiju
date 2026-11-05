
package com.kaiju.inventario.viewmodel

import androidx.lifecycle.ViewModel
import com.kaiju.inventario.model.FormularioProductoEstado
import com.kaiju.inventario.model.Producto
import com.kaiju.inventario.repository.InventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InventarioViewModel : ViewModel() {

    private val repository = InventarioRepository()

    val productos = repository.productos
    val movimientos = repository.movimientos

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
        val producto = repository.buscarProducto(codigo)
            ?: return

        codigoEnEdicion = producto.codigo

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
            repository.buscarProducto(datos.codigo.trim()) != null
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

    fun guardarProducto(): Boolean {
        if (!validarFormulario()) return false

        val datos = _formulario.value

        val productoAnterior = codigoEnEdicion?.let {
            repository.buscarProducto(it)
        }

        if (codigoEnEdicion != null && productoAnterior == null) {
            return false
        }

        val producto = Producto(
            codigo = productoAnterior?.codigo ?: datos.codigo.trim(),
            nombre = datos.nombre.trim(),
            descripcion = datos.descripcion.trim(),
            categoria = datos.categoria,
            tipo = datos.tipo.trim(),
            precio = datos.precio.toInt(),
            stockActual = productoAnterior?.stockActual
                ?: datos.stock.toInt(),
            stockMinimo = datos.stockMinimo.toInt(),
            detalle = datos.detalle.trim().ifBlank { null }
        )

        val guardado = if (productoAnterior != null) {
            repository.editarProducto(producto)
        } else {
            repository.agregarProducto(producto)
        }

        if (guardado) {
            prepararCreacion()
        }

        return guardado
    }

    fun limpiarFormulario() {
        prepararCreacion()
    }
}
