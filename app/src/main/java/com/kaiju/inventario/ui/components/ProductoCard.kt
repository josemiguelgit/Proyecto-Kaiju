package com.kaiju.inventario.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaiju.inventario.model.Producto

@Composable
fun ProductoCard(producto: Producto) {

    val stockBajo = producto.stock <= producto.stockMinimo

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Código: ${producto.codigo}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Categoría: ${producto.categoria}"
            )

            Text(
                text = "Precio: $${producto.precio}"
            )

            Text(
                text = "Stock disponible: ${producto.stock}"
            )

            if (stockBajo) {
                Text(
                    text = "Stock bajo",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}