
package com.kaiju.inventario.repository

import com.kaiju.inventario.model.Movimiento
import com.kaiju.inventario.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InventarioRepository {

    private val _productos = MutableStateFlow(
        DatosDePrueba.productos.toList()
    )

    val productos = _productos.asStateFlow()

    private val _movimientos =
        MutableStateFlow<List<Movimiento>>(emptyList())

    val movimientos = _movimientos.asStateFlow()

    fun buscarProducto(codigo: String): Producto? {
        return _productos.value.find {
            it.codigo.equals(codigo, ignoreCase = true)
        }
    }

    fun agregarProducto(producto: Producto): Boolean {

        if (producto.codigo.isBlank() ||
            buscarProducto(producto.codigo) != null ||
            producto.precio <= 0 ||
            producto.stockActual < 0 ||
            producto.stockMinimo < 0
        ) {
            return false
        }

        _productos.value = _productos.value + producto

        return true
    }
}
