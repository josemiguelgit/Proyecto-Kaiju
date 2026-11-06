
package com.kaiju.inventario.data.local

import com.kaiju.inventario.model.Movimiento
import com.kaiju.inventario.model.Producto
import com.kaiju.inventario.model.SentidoAjuste
import com.kaiju.inventario.model.TipoMovimiento

fun Producto.toEntity() = ProductoEntity(
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion,
    categoria = categoria,
    tipo = tipo,
    precio = precio,
    stockActual = stockActual,
    stockMinimo = stockMinimo,
    detalle = detalle
)

fun ProductoEntity.toModel() = Producto(
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion,
    categoria = categoria,
    tipo = tipo,
    precio = precio,
    stockActual = stockActual,
    stockMinimo = stockMinimo,
    detalle = detalle
)

fun Movimiento.toEntity() = MovimientoEntity(
    id = id,
    codigoProducto = codigoProducto,
    tipo = tipo.name,
    cantidad = cantidad,
    motivo = motivo,
    fecha = fecha,
    usuario = usuario,
    sentidoAjuste = sentidoAjuste?.name
)

fun MovimientoEntity.toModel() = Movimiento(
    id = id,
    codigoProducto = codigoProducto,
    tipo = TipoMovimiento.valueOf(tipo),
    cantidad = cantidad,
    motivo = motivo,
    fecha = fecha,
    usuario = usuario,
    sentidoAjuste = sentidoAjuste?.let {
        SentidoAjuste.valueOf(it)
    }
)
