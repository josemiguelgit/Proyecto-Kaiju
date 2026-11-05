package com.kaiju.inventario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaiju.inventario.model.Producto
import com.kaiju.inventario.ui.components.ProductoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen() {

    val productos = remember {
        listOf(
            Producto("K001", "Teclado mecánico", "Periféricos", 25000, 10, 3),
            Producto("K002", "Mouse gamer", "Periféricos", 15000, 2, 5),
            Producto("K003", "Cable HDMI", "Accesorios", 7990, 18, 4),
            Producto("K004", "Audífonos USB", "Audio", 19990, 1, 3)
        )
    }

    var soloStockBajo by remember {
        mutableStateOf(false)
    }

    val productosVisibles = if (soloStockBajo) {
        productos.filter { it.stock <= it.stockMinimo }
    } else {
        productos
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("KaijuApp")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Catálogo de productos",
                style = MaterialTheme.typography.headlineSmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mostrar solo stock bajo")

                Switch(
                    checked = soloStockBajo,
                    onCheckedChange = {
                        soloStockBajo = it
                    }
                )
            }

            Text(
                text = "Productos encontrados: ${productosVisibles.size}"
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {

                items(
                    items = productosVisibles,
                    key = { it.codigo }
                ) { producto ->

                    ProductoCard(producto = producto)

                }
            }
        }
    }
}