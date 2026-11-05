
package com.kaiju.inventario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaiju.inventario.ui.components.ProductoCard
import com.kaiju.inventario.viewmodel.InventarioViewModel

@Composable
fun CatalogoScreen(
    viewModel: InventarioViewModel,
    onProductoClick: (String) -> Unit
) {
    val productos by viewModel.productos.collectAsState()

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    var categoriaSeleccionada by rememberSaveable {
        mutableStateOf("Todas")
    }

    var soloStockBajo by rememberSaveable {
        mutableStateOf(false)
    }

    // Obtener categorías disponibles
    val categorias = listOf("Todas") +
            productos.map { it.categoria }
                .distinct()
                .sorted()

    // Aplicar búsqueda y filtros
    val productosVisibles = productos.filter { producto ->

        val coincideBusqueda =
            producto.nombre.contains(
                busqueda.trim(),
                ignoreCase = true
            ) ||
                    producto.codigo.contains(
                        busqueda.trim(),
                        ignoreCase = true
                    )

        val coincideCategoria =
            categoriaSeleccionada == "Todas" ||
                    producto.categoria == categoriaSeleccionada

        val coincideStock =
            !soloStockBajo ||
                    producto.stockActual <= producto.stockMinimo

        coincideBusqueda &&
                coincideCategoria &&
                coincideStock
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

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar producto") },
            placeholder = {
                Text("Nombre o código")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            text = "Categorías",
            style = MaterialTheme.typography.titleSmall
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categorias) { categoria ->

                FilterChip(
                    selected =
                        categoriaSeleccionada == categoria,
                    onClick = {
                        categoriaSeleccionada = categoria
                    },
                    label = {
                        Text(categoria)
                    }
                )
            }
        }

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

                ProductoCard(
                    producto = producto,
                    onClick = {
                        onProductoClick(producto.codigo)
                    }
                )
            }
        }
    }
}
