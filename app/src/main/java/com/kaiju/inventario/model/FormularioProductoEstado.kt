package com.kaiju.inventario.model

data class FormularioProductoEstado(
    val codigo: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val precio: String = "",
    val stock: String = "",
    val stockMinimo: String = "",
    val errores: Map<String, String> = emptyMap()
)