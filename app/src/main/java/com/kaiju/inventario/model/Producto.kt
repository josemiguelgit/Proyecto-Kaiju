package com.kaiju.inventario.model

data class Producto(
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val precio: Int,
    val stock: Int,
    val stockMinimo: Int
)