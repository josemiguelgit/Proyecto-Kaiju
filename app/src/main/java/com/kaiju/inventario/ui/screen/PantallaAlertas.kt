
package com.kaiju.inventario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaiju.inventario.viewmodel.InventarioViewModel

@Composable
fun PantallaAlertas(
    viewModel: InventarioViewModel,
    onProductoClick: (String) -> Unit
) {
    val productos by viewModel.productos.collectAsState()

    val productosEnAlerta = productos.filter {
        it.stockActual <= it.stockMinimo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Alertas de inventario",
            style = MaterialTheme.typography.headlineSmall
        )

        productosEnAlerta.forEach { producto ->
            OutlinedCard(
                onClick = {
                    onProductoClick(producto.codigo)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(producto.nombre)
                    Text(
                        "Stock: ${producto.stockActual} / " +
                                "Mínimo: ${producto.stockMinimo}"
                    )
                }
            }
        }
    }
}
