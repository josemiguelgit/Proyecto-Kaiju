
package com.kaiju.inventario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaiju.inventario.viewmodel.InventarioViewModel

@Composable
fun PantallaDetalleProducto(
    codigo: String,
    viewModel: InventarioViewModel
) {
    val productos by viewModel.productos.collectAsState()
    val producto = productos.find { it.codigo == codigo }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (producto == null) {
            Text("Producto no encontrado")
        } else {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.headlineSmall
            )

            Text("Código: ${producto.codigo}")
            Text("Descripción: ${producto.descripcion}")
            Text("Categoría: ${producto.categoria}")
            Text("Tipo: ${producto.tipo}")
            Text("Precio: $${producto.precio}")
            Text("Stock actual: ${producto.stockActual}")
            Text("Stock mínimo: ${producto.stockMinimo}")

            producto.detalle?.let {
                Text("Detalle: $it")
            }

            HorizontalDivider()

            Text(
                text = "Historial de movimientos",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Todavía no se han registrado movimientos.")
        }
    }
}
