package com.kaiju.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.kaiju.inventario.navigation.Rutas
import com.kaiju.inventario.ui.screen.CatalogoScreen
import com.kaiju.inventario.ui.screen.FormularioProductoScreen
import com.kaiju.inventario.ui.theme.KaijuAppTheme
import com.kaiju.inventario.viewmodel.ProductoViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val productoViewModel = ViewModelProvider(this)[
            ProductoViewModel::class.java
        ]

        setContent {
            KaijuAppTheme {
                KaijuApp(productoViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaijuApp(viewModel: ProductoViewModel) {

    var rutaActual by rememberSaveable {
        mutableStateOf(Rutas.Catalogo.ruta)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KaijuApp") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },

        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = rutaActual == Rutas.Catalogo.ruta,
                    onClick = {
                        rutaActual = Rutas.Catalogo.ruta
                    },
                    icon = {
                        Text("▤")
                    },
                    label = {
                        Text("Catálogo")
                    }
                )

                NavigationBarItem(
                    selected = rutaActual == Rutas.AgregarProducto.ruta,
                    onClick = {
                        rutaActual = Rutas.AgregarProducto.ruta
                    },
                    icon = {
                        Text("+")
                    },
                    label = {
                        Text("Agregar")
                    }
                )
            }
        }

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (rutaActual) {

                Rutas.Catalogo.ruta -> {
                    CatalogoScreen(
                        viewModel = viewModel
                    )
                }

                Rutas.AgregarProducto.ruta -> {
                    FormularioProductoScreen(
                        viewModel = viewModel,
                        onProductoGuardado = {
                            rutaActual = Rutas.Catalogo.ruta
                        }
                    )
                }
            }
        }
    }
}