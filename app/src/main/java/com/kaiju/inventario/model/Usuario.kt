
package com.kaiju.inventario.model

enum class Rol {
    ADMINISTRADOR,
    VENDEDOR,
    ENCARGADO_INVENTARIO
}

data class Usuario(
    val id: String,
    val nombre: String,
    val rol: Rol
)
