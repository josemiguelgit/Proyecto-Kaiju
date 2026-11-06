
package com.kaiju.inventario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaiju.inventario.viewmodel.InventarioViewModel
import com.kaiju.inventario.model.TipoMovimiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun PantallaDetalleProducto(
    codigo: String,
    viewModel: InventarioViewModel,
    puedeEditar: Boolean,
    onEditar: (String) -> Unit
) {
    val productos by viewModel.productos.collectAsState()
    val producto = productos.find { it.codigo == codigo }
    val movimientos by viewModel.movimientos.collectAsState()
    val historial = movimientos
        .filter { it.codigoProducto == codigo }
        .sortedByDescending { it.fecha }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            ,
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

            if (puedeEditar) {
                Button(
                    onClick = {
                        onEditar(producto.codigo)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar producto")
                }
            }

            HorizontalDivider()

            Text(
                text = "Historial de movimientos",
                style = MaterialTheme.typography.titleMedium
            )


            if (historial.isEmpty()) {

                Text("Todavía no se han registrado movimientos.")

            } else {

                historial.forEach { movimiento ->

                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {

                            val fecha = SimpleDateFormat(
                                "dd/MM/yyyy HH:mm",
                                Locale.getDefault()
                            ).format(Date(movimiento.fecha))

                            Text(
                                text = movimiento.tipo.name.replace("_", " "),
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text("Cantidad: ${movimiento.cantidad}")
                            Text("Fecha: $fecha")
                            Text("Usuario: ${movimiento.usuario}")

                            if (movimiento.tipo == TipoMovimiento.AJUSTE) {

                                Text(
                                    "Sentido: ${
                                        movimiento.sentidoAjuste?.name ?: "Sin definir"
                                    }"
                                )

                                movimiento.motivo?.let {
                                    Text("Motivo: $it")
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}
