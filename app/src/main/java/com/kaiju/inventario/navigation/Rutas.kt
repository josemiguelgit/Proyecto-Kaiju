package com.kaiju.inventario.navigation

sealed class Rutas(val ruta: String) {

    data object Catalogo : Rutas("catalogo")

    data object AgregarProducto : Rutas("agregar_producto")
}