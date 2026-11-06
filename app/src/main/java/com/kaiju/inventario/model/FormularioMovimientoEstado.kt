
package com.kaiju.inventario.model

data class FormularioMovimientoEstado(
    val codigoProducto: String = "",
    val tipo: String = "",
    val cantidad: String = "",
    val sentidoAjuste: String = "",
    val motivo: String = "",
    val errores: Map<String, String> = emptyMap()
)
