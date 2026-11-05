
package com.kaiju.inventario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaiju.inventario.viewmodel.SesionViewModel

@Composable
fun PantallaLogin(
    viewModel: SesionViewModel,
    onLoginCorrecto: () -> Unit
) {
    val usuarios by viewModel.usuarios.collectAsState()
    val error by viewModel.errorLogin.collectAsState()

    var usuarioSeleccionado by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Bienvenido a KaijuApp",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Seleccione su usuario de prueba para ingresar.")

        usuarios.forEach { usuario ->

            val seleccionado =
                usuarioSeleccionado == usuario.id

            Card(
                onClick = {
                    usuarioSeleccionado = usuario.id
                },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (seleccionado) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected = seleccionado,
                        onClick = {
                            usuarioSeleccionado = usuario.id
                        }
                    )

                    Column {
                        Text(
                            text = usuario.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = usuario.rol.name.replace("_", " "),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        if (error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                if (viewModel.iniciarSesion(usuarioSeleccionado)) {
                    onLoginCorrecto()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
    }
}
