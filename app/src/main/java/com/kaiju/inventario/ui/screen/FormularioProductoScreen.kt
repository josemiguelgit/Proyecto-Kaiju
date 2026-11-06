
package com.kaiju.inventario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kaiju.inventario.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductoScreen(
    viewModel: InventarioViewModel,
    onProductoGuardado: () -> Unit,
    codigoEdicion: String? = null
) {
    val formulario by viewModel.formulario.collectAsState()

    LaunchedEffect(codigoEdicion) {
        if (codigoEdicion == null) {
            viewModel.prepararCreacion()
        } else {
            viewModel.prepararEdicion(codigoEdicion)
        }
    }

    val editando = codigoEdicion != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = if (editando) "Editar producto"
            else "Registrar producto",
            style = MaterialTheme.typography.headlineSmall
        )

        CampoProducto(
            valor = formulario.codigo,
            onCambio = { viewModel.actualizarCampo("codigo", it) },
            etiqueta = "Código",
            error = formulario.errores["codigo"],
            ayuda = "Identificador único",
            soloLectura = editando
        )

        CampoProducto(
            valor = formulario.nombre,
            onCambio = { viewModel.actualizarCampo("nombre", it) },
            etiqueta = "Nombre",
            error = formulario.errores["nombre"]
        )

        CampoProducto(
            valor = formulario.descripcion,
            onCambio = { viewModel.actualizarCampo("descripcion", it) },
            etiqueta = "Descripción",
            error = formulario.errores["descripcion"],
            unaLinea = false,
            ayuda = "Descripción de la prenda"
        )

        var desplegado by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = desplegado,
            onExpandedChange = { desplegado = it }
        ) {
            OutlinedTextField(
                value = formulario.categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                isError = formulario.errores.containsKey("categoria"),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = desplegado
                    )
                },
                supportingText = {
                    Text(
                        formulario.errores["categoria"]
                            ?: "Seleccione una categoría"
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = desplegado,
                onDismissRequest = { desplegado = false }
            ) {
                viewModel.categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria) },
                        onClick = {
                            viewModel.actualizarCampo(
                                "categoria",
                                categoria
                            )
                            desplegado = false
                        }
                    )
                }
            }
        }

        CampoProducto(
            valor = formulario.tipo,
            onCambio = { viewModel.actualizarCampo("tipo", it) },
            etiqueta = "Tipo de prenda",
            error = formulario.errores["tipo"],
            ayuda = "Ejemplo: ropa casual"
        )

        CampoProducto(
            valor = formulario.precio,
            onCambio = { viewModel.actualizarCampo("precio", it) },
            etiqueta = "Precio",
            error = formulario.errores["precio"],
            teclado = KeyboardType.Number,
            ayuda = "Precio entero en pesos chilenos"
        )

        CampoProducto(
            valor = formulario.stock,
            onCambio = { viewModel.actualizarCampo("stock", it) },
            etiqueta = "Stock actual",
            error = formulario.errores["stock"],
            teclado = KeyboardType.Number,
            soloLectura = editando,
            ayuda = if (editando) {
                "Las existencias se corrigen mediante movimientos"
            } else {
                "Cantidad inicial del producto"
            }
        )

        CampoProducto(
            valor = formulario.stockMinimo,
            onCambio = {
                viewModel.actualizarCampo("stockMinimo", it)
            },
            etiqueta = "Stock mínimo",
            error = formulario.errores["stockMinimo"],
            teclado = KeyboardType.Number
        )

        CampoProducto(
            valor = formulario.detalle,
            onCambio = { viewModel.actualizarCampo("detalle", it) },
            etiqueta = "Detalle adicional",
            error = formulario.errores["detalle"],
            ayuda = "Opcional: talla, color, lote, etc."
        )

        formulario.errores["general"]?.let { mensaje ->
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                viewModel.guardarProducto { guardado ->
                    if (guardado) {
                        onProductoGuardado()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (editando) "Guardar cambios"
                else "Registrar producto"
            )
        }
    }
}

@Composable
private fun CampoProducto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    ayuda: String = "",
    teclado: KeyboardType = KeyboardType.Text,
    soloLectura: Boolean = false,
    unaLinea: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = soloLectura,
        singleLine = unaLinea,
        keyboardOptions = KeyboardOptions(
            keyboardType = teclado
        ),
        isError = error != null,
        trailingIcon = {
            if (error != null) {
                Text(
                    "!",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        supportingText = {
            Text(error ?: ayuda)
        }
    )
}
