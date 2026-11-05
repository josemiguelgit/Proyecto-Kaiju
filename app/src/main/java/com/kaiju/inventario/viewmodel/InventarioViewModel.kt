package com.kaiju.inventario.viewmodel

import androidx.lifecycle.ViewModel
import com.kaiju.inventario.model.Producto
import com.kaiju.inventario.model.FormularioProductoEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.kaiju.inventario.repository.DatosDePrueba
import com.kaiju.inventario.repository.InventarioRepository

class InventarioViewModel : ViewModel() {

    private val repository = InventarioRepository()

    val productos = repository.productos

    val movimientos = repository.movimientos

    private val _formulario = MutableStateFlow(
        FormularioProductoEstado()
    )

    val formulario: StateFlow<FormularioProductoEstado> =
        _formulario.asStateFlow()

    // Actualizar campos del formulario
    fun actualizarCodigo(valor: String) {
        _formulario.value = _formulario.value.copy(
            codigo = valor,
            errores = emptyMap()
        )
    }

    fun actualizarNombre(valor: String) {
        _formulario.value = _formulario.value.copy(
            nombre = valor,
            errores = emptyMap()
        )
    }

    fun actualizarCategoria(valor: String) {
        _formulario.value = _formulario.value.copy(
            categoria = valor,
            errores = emptyMap()
        )
    }

    fun actualizarPrecio(valor: String) {
        _formulario.value = _formulario.value.copy(
            precio = valor,
            errores = emptyMap()
        )
    }

    fun actualizarStock(valor: String) {
        _formulario.value = _formulario.value.copy(
            stock = valor,
            errores = emptyMap()
        )
    }

    fun actualizarStockMinimo(valor: String) {
        _formulario.value = _formulario.value.copy(
            stockMinimo = valor,
            errores = emptyMap()
        )
    }

    // Validaciones del formulario
    fun validarFormulario(): Boolean {

        val datos = _formulario.value
        val errores = mutableMapOf<String, String>()

        if (datos.codigo.isBlank()) {
            errores["codigo"] = "El código es obligatorio"
        } else if (
            repository.buscarProducto(datos.codigo.trim()) != null
        ) {
            errores["codigo"] = "El código ya existe"
        }

        if (datos.nombre.isBlank()) {
            errores["nombre"] = "Ingrese el nombre del producto"
        }

        if (datos.categoria.isBlank()) {
            errores["categoria"] = "Ingrese una categoría"
        }

        val precio = datos.precio.toIntOrNull()

        if (precio == null || precio <= 0) {
            errores["precio"] = "El precio debe ser mayor que cero"
        }

        val stock = datos.stock.toIntOrNull()

        if (stock == null || stock < 0) {
            errores["stock"] = "El stock no puede ser negativo"
        }

        val minimo = datos.stockMinimo.toIntOrNull()

        if (minimo == null || minimo < 0) {
            errores["stockMinimo"] =
                "El stock mínimo debe ser mayor o igual a cero"
        }

        _formulario.value = datos.copy(errores = errores)

        return errores.isEmpty()
    }

    // Guardar producto
    fun guardarProducto(): Boolean {

        if (!validarFormulario()) {
            return false
        }

        val datos = _formulario.value

        val nuevoProducto = Producto(
            codigo = datos.codigo.trim(),
            nombre = datos.nombre.trim(),
            descripcion = "",
            categoria = datos.categoria.trim(),
            tipo = "General",
            precio = datos.precio.toInt(),
            stockActual = datos.stock.toInt(),
            stockMinimo = datos.stockMinimo.toInt(),
            detalle = null
        )

        val guardado = repository.agregarProducto(nuevoProducto)

        if (guardado) {
            limpiarFormulario()
        }

        return guardado
    }

    fun limpiarFormulario() {
        _formulario.value = FormularioProductoEstado()
    }
}