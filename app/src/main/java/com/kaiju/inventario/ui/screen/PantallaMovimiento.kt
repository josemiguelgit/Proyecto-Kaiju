
package com.kaiju.inventario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

import com.kaiju.inventario.model.Rol
import com.kaiju.inventario.model.TipoMovimiento
import com.kaiju.inventario.model.Usuario
import com.kaiju.inventario.viewmodel.InventarioViewModel
import androidx.compose.ui.platform.LocalContext
import com.kaiju.inventario.utils.VibracionUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMovimiento(
    viewModel: InventarioViewModel,
    usuario: Usuario
) {
    val formulario by viewModel.formularioMovimiento.collectAsState()
    val productos by viewModel.productos.collectAsState()
    val context = LocalContext.current

    var mensajeExito by remember {
        mutableStateOf(false)
    }

    var menuProductos by remember { mutableStateOf(false) }
    var menuTipos by remember { mutableStateOf(false) }

    val tiposPermitidos = when (usuario.rol) {
        Rol.ADMINISTRADOR -> TipoMovimiento.entries

        Rol.VENDEDOR -> listOf(
            TipoMovimiento.SALIDA_VENTA
        )

        Rol.ENCARGADO_INVENTARIO -> listOf(
            TipoMovimiento.ENTRADA,
            TipoMovimiento.SALIDA_CONSUMO,
            TipoMovimiento.AJUSTE
        )
    }

    val productoSeleccionado = productos.find {
        it.codigo == formulario.codigoProducto
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            "Registrar movimiento",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("Usuario: ${usuario.nombre}")

        // Seleccionar producto
        ExposedDropdownMenuBox(
            expanded = menuProductos,
            onExpandedChange = { menuProductos = it }
        ) {
            OutlinedTextField(
                value = productoSeleccionado?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Producto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuProductos
                    )
                },
                isError = formulario.errores.containsKey(
                    "codigoProducto"
                ),
                supportingText = {
                    Text(
                        formulario.errores["codigoProducto"]
                            ?: "Seleccione una prenda"
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = menuProductos,
                onDismissRequest = { menuProductos = false }
            ) {
                productos.forEach { producto ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                "${producto.nombre} " +
                                        "(${producto.codigo})"
                            )
                        },
                        onClick = {
                            viewModel.actualizarCampoMovimiento(
                                "codigoProducto",
                                producto.codigo
                            )
                            mensajeExito = false
                            menuProductos = false
                        }
                    )
                }
            }
        }

        if (productoSeleccionado != null) {
            Text(
                "Stock actual: ${productoSeleccionado.stockActual}"
            )
        }

        // Tipo de movimiento
        ExposedDropdownMenuBox(
            expanded = menuTipos,
            onExpandedChange = { menuTipos = it }
        ) {
            OutlinedTextField(
                value = formulario.tipo.replace("_", " "),
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de movimiento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuTipos
                    )
                },
                isError = formulario.errores.containsKey("tipo"),
                supportingText = {
                    Text(
                        formulario.errores["tipo"]
                            ?: "Seleccione una operación"
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = menuTipos,
                onDismissRequest = { menuTipos = false }
            ) {
                tiposPermitidos.forEach { tipo ->
                    DropdownMenuItem(
                        text = {
                            Text(tipo.name.replace("_", " "))
                        },
                        onClick = {
                            viewModel.actualizarCampoMovimiento(
                                "tipo",
                                tipo.name
                            )
                            mensajeExito = false
                            menuTipos = false
                        }
                    )
                }
            }
        }

        // Cantidad
        OutlinedTextField(
            value = formulario.cantidad,
            onValueChange = {
                viewModel.actualizarCampoMovimiento(
                    "cantidad",
                    it
                )
                mensajeExito = false
            },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = formulario.errores.containsKey("cantidad"),
            supportingText = {
                Text(
                    formulario.errores["cantidad"]
                        ?: "Ingrese unidades"
                )
            },
            trailingIcon = {
                if (formulario.errores.containsKey("cantidad")) {
                    Text(
                        "!",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        if (formulario.tipo == TipoMovimiento.AJUSTE.name) {

            Text(
                "Sentido del ajuste",
                style = MaterialTheme.typography.titleSmall
            )

            Row {
                RadioButton(
                    selected = formulario.sentidoAjuste == "AUMENTO",
                    onClick = {
                        viewModel.actualizarCampoMovimiento(
                            "sentidoAjuste",
                            "AUMENTO"
                        )
                    }
                )
                Text(
                    "Aumentar",
                    modifier = Modifier.padding(top = 12.dp)
                )

                Spacer(Modifier.width(12.dp))

                RadioButton(
                    selected = formulario.sentidoAjuste == "DISMINUCION",
                    onClick = {
                        viewModel.actualizarCampoMovimiento(
                            "sentidoAjuste",
                            "DISMINUCION"
                        )
                    }
                )
                Text(
                    "Disminuir",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            formulario.errores["sentidoAjuste"]?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = formulario.motivo,
                onValueChange = {
                    viewModel.actualizarCampoMovimiento(
                        "motivo",
                        it
                    )
                },
                label = { Text("Motivo del ajuste") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                isError = formulario.errores.containsKey("motivo"),
                supportingText = {
                    Text(
                        formulario.errores["motivo"]
                            ?: "Explique por qué se realiza el ajuste"
                    )
                }
            )
        }

        formulario.errores["general"]?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (mensajeExito) {
            Text(
                "Movimiento registrado correctamente",
                color = MaterialTheme.colorScheme.primary
            )
        }


        Button(
            onClick = {

                viewModel.guardarMovimiento(usuario) { guardado ->

                    mensajeExito = guardado

                    if (!guardado) {

                        val errores =
                            viewModel.formularioMovimiento.value.errores

                        val errorCantidad =
                            errores["cantidad"].orEmpty()

                        val errorGeneral =
                            errores["general"].orEmpty()

                        val stockInsuficiente =
                            errorCantidad.startsWith("Stock insuficiente") ||
                                    errorGeneral.startsWith("Stock insuficiente")

                        if (stockInsuficiente) {
                            VibracionUtils.vibrarError(context)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar movimiento")
        }

    }
}
