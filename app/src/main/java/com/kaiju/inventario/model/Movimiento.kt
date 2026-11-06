package com.kaiju.inventario.model

enum class TipoMovimiento {
    ENTRADA,
    SALIDA_VENTA,
    SALIDA_CONSUMO,
    AJUSTE
}

enum class SentidoAjuste {
    AUMENTO,
    DISMINUCION
}

data class Movimiento(
    val id: String,
    val codigoProducto: String,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val motivo: String? = null,
    val fecha: Long,
    val usuario: String,
    val sentidoAjuste: SentidoAjuste? = null
)
