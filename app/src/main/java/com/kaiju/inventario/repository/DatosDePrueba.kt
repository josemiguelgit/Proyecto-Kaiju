
package com.kaiju.inventario.repository

import com.kaiju.inventario.model.Producto
import com.kaiju.inventario.model.Rol
import com.kaiju.inventario.model.Usuario

object DatosDePrueba {
    val productos = listOf(
        Producto(
            codigo = "K001",
            nombre = "Polera básica algodón",
            descripcion = "Polera de algodón unisex",
            categoria = "Poleras",
            tipo = "Ropa casual",
            precio = 9990,
            stockActual = 15,
            stockMinimo = 5,
            detalle = "Talla M, color blanco"
        ),
        Producto(
            codigo = "K002",
            nombre = "Jeans slim fit",
            descripcion = "Pantalón de mezclilla ajustado",
            categoria = "Pantalones",
            tipo = "Ropa casual",
            precio = 24990,
            stockActual = 3,
            stockMinimo = 5,
            detalle = "Talla 42, color azul"
        ),
        Producto(
            codigo = "K003",
            nombre = "Polerón con capucha",
            descripcion = "Polerón de algodón con bolsillo frontal",
            categoria = "Polerones",
            tipo = "Ropa urbana",
            precio = 29990,
            stockActual = 12,
            stockMinimo = 4,
            detalle = "Talla L, color negro"
        ),
        Producto(
            codigo = "K004",
            nombre = "Chaqueta denim",
            descripcion = "Chaqueta de mezclilla clásica",
            categoria = "Chaquetas",
            tipo = "Ropa casual",
            precio = 39990,
            stockActual = 2,
            stockMinimo = 3,
            detalle = "Talla M, color azul"
        ),
        Producto(
            codigo = "K005",
            nombre = "Camisa manga larga",
            descripcion = "Camisa de algodón para uso diario",
            categoria = "Camisas",
            tipo = "Ropa formal",
            precio = 19990,
            stockActual = 9,
            stockMinimo = 3,
            detalle = "Talla L, color celeste"
        ),
        Producto(
            codigo = "K006",
            nombre = "Pantalón cargo",
            descripcion = "Pantalón con bolsillos laterales",
            categoria = "Pantalones",
            tipo = "Ropa urbana",
            precio = 27990,
            stockActual = 6,
            stockMinimo = 3,
            detalle = "Talla 44, color verde"
        ),
        Producto(
            codigo = "K007",
            nombre = "Vestido casual",
            descripcion = "Vestido ligero de uso diario",
            categoria = "Vestidos",
            tipo = "Ropa casual",
            precio = 22990,
            stockActual = 4,
            stockMinimo = 4,
            detalle = "Talla S, color rojo"
        ),
        Producto(
            codigo = "K008",
            nombre = "Parka impermeable",
            descripcion = "Parka para clima frío y lluvioso",
            categoria = "Chaquetas",
            tipo = "Ropa de invierno",
            precio = 54990,
            stockActual = 0,
            stockMinimo = 3,
            detalle = "Talla XL, color negro"
        ),
        Producto(
            codigo = "K009",
            nombre = "Short deportivo",
            descripcion = "Short ligero para actividad física",
            categoria = "Shorts",
            tipo = "Ropa deportiva",
            precio = 14990,
            stockActual = 18,
            stockMinimo = 5,
            detalle = "Talla M, color gris"
        ),
        Producto(
            codigo = "K010",
            nombre = "Falda plisada",
            descripcion = "Falda de tela plisada",
            categoria = "Faldas",
            tipo = "Ropa casual",
            precio = 17990,
            stockActual = 5,
            stockMinimo = 2,
            detalle = "Talla S, color beige"
        )
    )

    val usuarios = listOf(
        Usuario(
            id = "U001",
            nombre = "Administrador Demo",
            rol = Rol.ADMINISTRADOR
        ),
        Usuario(
            id = "U002",
            nombre = "Vendedor Demo",
            rol = Rol.VENDEDOR
        ),
        Usuario(
            id = "U003",
            nombre = "Inventario Demo",
            rol = Rol.ENCARGADO_INVENTARIO
        )
    )



}
