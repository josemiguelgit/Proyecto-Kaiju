package com.kaiju.inventario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.kaiju.inventario.viewmodel.ProductoViewModel

@Composable
fun FormularioProductoScreen(
    viewModel: ProductoViewModel,
    onProductoGuardado: () -> Unit
) {

    val formulario by viewModel.formulario.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Registrar producto",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = formulario.codigo,
            onValueChange = viewModel::actualizarCodigo,
            label = { Text("Código") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = formulario.errores.containsKey("codigo"),
            supportingText = {
                Text(formulario.errores["codigo"] ?: "Código único del producto")
            }
        )

        OutlinedTextField(
            value = formulario.nombre,
            onValueChange = viewModel::actualizarNombre,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = formulario.errores.containsKey("nombre"),
            supportingText = {
                Text(formulario.errores["nombre"] ?: "")
            }
        )

        OutlinedTextField(
            value = formulario.categoria,
            onValueChange = viewModel::actualizarCategoria,
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = formulario.errores.containsKey("categoria"),
            supportingText = {
                Text(formulario.errores["categoria"] ?: "")
            }
        )

        OutlinedTextField(
            value = formulario.precio,
            onValueChange = viewModel::actualizarPrecio,
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = formulario.errores.containsKey("precio"),
            supportingText = {
                Text(formulario.errores["precio"] ?: "Precio en pesos chilenos")
            }
        )

        OutlinedTextField(
            value = formulario.stock,
            onValueChange = viewModel::actualizarStock,
            label = { Text("Stock disponible") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = formulario.errores.containsKey("stock"),
            supportingText = {
                Text(formulario.errores["stock"] ?: "")
            }
        )

        OutlinedTextField(
            value = formulario.stockMinimo,
            onValueChange = viewModel::actualizarStockMinimo,
            label = { Text("Stock mínimo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = formulario.errores.containsKey("stockMinimo"),
            supportingText = {
                Text(formulario.errores["stockMinimo"] ?: "")
            }
        )

        Button(
            onClick = {
                if (viewModel.guardarProducto()) {
                    onProductoGuardado()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar producto")
        }
    }
}