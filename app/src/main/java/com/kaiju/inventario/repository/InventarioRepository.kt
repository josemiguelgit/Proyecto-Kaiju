
package com.kaiju.inventario.repository

import androidx.room.withTransaction
import com.kaiju.inventario.data.local.KaijuDatabase
import com.kaiju.inventario.data.local.toEntity
import com.kaiju.inventario.data.local.toModel
import com.kaiju.inventario.model.*
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.map

class InventarioRepository(
    private val database: KaijuDatabase
) {

    private val productoDao = database.productoDao()
    private val movimientoDao = database.movimientoDao()

    val productos = productoDao.observarProductos().map { lista ->
        lista.map { it.toModel() }
    }

    val movimientos = movimientoDao.observarMovimientos().map { lista ->
        lista.map { it.toModel() }
    }

    // Cargar prendas ficticias solo si la BD está vacía
    suspend fun inicializarDatos() {
        database.withTransaction {
            if (productoDao.contarProductos() == 0) {
                productoDao.insertarTodos(
                    DatosDePrueba.productos.map { it.toEntity() }
                )
            }
        }
    }

    suspend fun buscarProducto(codigo: String): Producto? {
        return productoDao.buscarPorCodigo(codigo)?.toModel()
    }

    suspend fun agregarProducto(producto: Producto): Boolean {
        if (
            producto.codigo.isBlank() ||
            producto.nombre.trim().length < 3 ||
            producto.precio <= 0 ||
            producto.stockActual < 0 ||
            producto.stockMinimo < 0
        ) {
            return false
        }

        return try {
            database.withTransaction {
                if (buscarProducto(producto.codigo) != null) {
                    false
                } else {
                    productoDao.insertar(producto.toEntity())
                    true
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    suspend fun editarProducto(producto: Producto): Boolean {
        return try {
            database.withTransaction {
                val anterior = buscarProducto(producto.codigo)

                if (
                    anterior == null ||
                    producto.precio <= 0 ||
                    producto.stockMinimo < 0 ||
                    anterior.stockActual != producto.stockActual
                ) {
                    false
                } else {
                    productoDao.actualizar(producto.toEntity()) == 1
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    suspend fun registrarMovimiento(
        codigoProducto: String,
        tipo: TipoMovimiento,
        cantidad: Int,
        motivo: String?,
        sentidoAjuste: SentidoAjuste?,
        usuario: Usuario
    ): String? {

        val autorizado = when (usuario.rol) {
            Rol.ADMINISTRADOR -> true

            Rol.VENDEDOR ->
                tipo == TipoMovimiento.SALIDA_VENTA

            Rol.ENCARGADO_INVENTARIO ->
                tipo != TipoMovimiento.SALIDA_VENTA
        }

        if (!autorizado) {
            return "No tiene permiso para esta operación"
        }

        if (cantidad <= 0) {
            return "La cantidad debe ser mayor que cero"
        }

        if (tipo == TipoMovimiento.AJUSTE) {
            if (motivo.isNullOrBlank()) {
                return "El ajuste exige un motivo"
            }

            if (sentidoAjuste == null) {
                return "Seleccione el sentido del ajuste"
            }
        }

        return try {
            database.withTransaction {

                val producto = buscarProducto(codigoProducto)
                    ?: return@withTransaction "Producto no encontrado"

                val variacion = when (tipo) {
                    TipoMovimiento.ENTRADA ->
                        cantidad.toLong()

                    TipoMovimiento.SALIDA_VENTA,
                    TipoMovimiento.SALIDA_CONSUMO ->
                        -cantidad.toLong()

                    TipoMovimiento.AJUSTE ->
                        if (sentidoAjuste == SentidoAjuste.AUMENTO) {
                            cantidad.toLong()
                        } else {
                            -cantidad.toLong()
                        }
                }

                val nuevoStock =
                    producto.stockActual.toLong() + variacion

                if (nuevoStock < 0) {
                    return@withTransaction "Stock insuficiente"
                }

                if (nuevoStock > Int.MAX_VALUE) {
                    return@withTransaction "Cantidad fuera de rango"
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
                    sentidoAjuste =
                        if (tipo == TipoMovimiento.AJUSTE) {
                            sentidoAjuste
                        } else {
                            null
                        }
                )

                val filas = productoDao.actualizar(
                    producto.copy(
                        stockActual = nuevoStock.toInt()
                    ).toEntity()
                )

                if (filas != 1) {
                    error("No se pudo actualizar el producto")
                }

                movimientoDao.insertar(movimiento.toEntity())

                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            "No se pudo guardar el movimiento"
        }
    }
}
