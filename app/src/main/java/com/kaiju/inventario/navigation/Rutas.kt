
package com.kaiju.inventario.navigation

sealed class Rutas(val ruta: String) {

    data object Login : Rutas("login")

    data object Catalogo : Rutas("catalogo")

    data object DetalleProducto : Rutas("detalle/{codigo}") {
        fun crearRuta(codigo: String): String {
            return "detalle/$codigo"
        }
    }

    data object AgregarProducto : Rutas("agregar_producto")

    data object Movimiento : Rutas("movimiento")

    data object Alertas : Rutas("alertas")
}
