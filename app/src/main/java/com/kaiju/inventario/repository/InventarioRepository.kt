
package com.kaiju.inventario.repository

import com.kaiju.inventario.model.Movimiento
import com.kaiju.inventario.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.kaiju.inventario.model.Rol
import com.kaiju.inventario.model.SentidoAjuste
import com.kaiju.inventario.model.TipoMovimiento
import com.kaiju.inventario.model.Usuario
import java.util.UUID

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


    fun editarProducto(producto: Producto): Boolean {

        val indice = _productos.value.indexOfFirst {
            it.codigo.equals(
                producto.codigo,
                ignoreCase = true
            )
        }

        if (indice == -1 ||
            producto.precio <= 0 ||
            producto.stockActual < 0 ||
            producto.stockMinimo < 0 ||
            producto.stockActual != _productos.value[indice].stockActual
        ) {
            return false
        }

        _productos.value = _productos.value.toMutableList().apply {
            this[indice] = producto
        }

        return true
    }


    @Synchronized
    fun registrarMovimiento(
        codigoProducto: String,
        tipo: TipoMovimiento,
        cantidad: Int,
        motivo: String?,
        sentidoAjuste: SentidoAjuste?,
        usuario: Usuario
    ): String? {

        // Permisos por perfil
        val autorizado = when (usuario.rol) {
            Rol.ADMINISTRADOR -> true

            Rol.VENDEDOR ->
                tipo == TipoMovimiento.SALIDA_VENTA

            Rol.ENCARGADO_INVENTARIO ->
                tipo != TipoMovimiento.SALIDA_VENTA
        }

        if (!autorizado) {
            return "El perfil no tiene permiso para esta operación"
        }

        val producto = buscarProducto(codigoProducto)
            ?: return "Debe seleccionar un producto válido"

        if (cantidad <= 0) {
            return "La cantidad debe ser mayor que cero"
        }

        if (tipo == TipoMovimiento.AJUSTE) {
            if (motivo.isNullOrBlank()) {
                return "Debe indicar el motivo del ajuste"
            }
            if (sentidoAjuste == null) {
                return "Debe seleccionar el sentido del ajuste"
            }
        }

        val variacion = when (tipo) {
            TipoMovimiento.ENTRADA -> cantidad.toLong()

            TipoMovimiento.SALIDA_VENTA,
            TipoMovimiento.SALIDA_CONSUMO -> -cantidad.toLong()

            TipoMovimiento.AJUSTE ->
                if (sentidoAjuste == SentidoAjuste.AUMENTO) {
                    cantidad.toLong()
                } else {
                    -cantidad.toLong()
                }
        }

        val nuevoStock = producto.stockActual.toLong() + variacion

        if (nuevoStock < 0) {
            return "Stock insuficiente para realizar la operación"
        }

        if (nuevoStock > Int.MAX_VALUE) {
            return "La cantidad supera el máximo permitido"
        }

        val movimiento = Movimiento(
            id = UUID.randomUUID().toString(),
            codigoProducto = producto.codigo,
            tipo = tipo,
            cantidad = cantidad,
            motivo = if (tipo == TipoMovimiento.AJUSTE) {
                motivo?.trim()
            } else {
                null
            },
            fecha = System.currentTimeMillis(),
            usuario = usuario.id,
            sentidoAjuste = if (tipo == TipoMovimiento.AJUSTE) {
                sentidoAjuste
            } else {
                null
            }
        )

        // Actualizar existencias y registrar trazabilidad
        _productos.value = _productos.value.map {
            if (it.codigo == producto.codigo) {
                it.copy(stockActual = nuevoStock.toInt())
            } else {
                it
            }
        }

        _movimientos.value = _movimientos.value + movimiento

        return null
    }


}
