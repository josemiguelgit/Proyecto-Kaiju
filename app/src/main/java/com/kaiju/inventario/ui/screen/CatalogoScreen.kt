package com.kaiju.inventario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaiju.inventario.ui.components.ProductoCard
import com.kaiju.inventario.viewmodel.ProductoViewModel

@Composable
fun CatalogoScreen(viewModel: ProductoViewModel) {

    val productos by viewModel.productos.collectAsState()

    var soloStockBajo by remember {
        mutableStateOf(false)
    }

    val productosVisibles = if (soloStockBajo) {
        productos.filter { it.stock <= it.stockMinimo }
    } else {
        productos
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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