
package com.kaiju.inventario.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.kaiju.inventario.model.Rol
import com.kaiju.inventario.ui.screen.*
import com.kaiju.inventario.viewmodel.InventarioViewModel
import com.kaiju.inventario.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    inventarioViewModel: InventarioViewModel,
    sesionViewModel: SesionViewModel
) {
    val navController = rememberNavController()

    val usuarioActivo by
    sesionViewModel.usuarioActivo.collectAsState()

    val backStack by
    navController.currentBackStackEntryAsState()

    val rutaActual = backStack?.destination?.route

    fun irCatalogo() {
        navController.navigate(Rutas.Catalogo.ruta) {
            popUpTo(Rutas.Catalogo.ruta) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    fun redirigirSiNoAutorizado() {
        if (usuarioActivo == null) {
            navController.navigate(Rutas.Login.ruta) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else {
            irCatalogo()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KaijuApp") },
                actions = {
                    if (usuarioActivo != null) {
                        TextButton(
                            onClick = {
                                sesionViewModel.cerrarSesion()
                                navController.navigate(Rutas.Login.ruta) {
                                    popUpTo(Rutas.Catalogo.ruta) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Salir")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                        MaterialTheme.colorScheme.primary,
                    titleContentColor =
                        MaterialTheme.colorScheme.onPrimary,

                )

            )
        },

        bottomBar = {
            if (usuarioActivo != null) {

                NavigationBar {

                    NavigationBarItem(
                        selected =
                            rutaActual == Rutas.Catalogo.ruta,
                        onClick = { irCatalogo() },
                        icon = { Text("▤") },
                        label = { Text("Catálogo") }
                    )

                    NavigationBarItem(
                        selected =
                            rutaActual == Rutas.Movimiento.ruta,
                        onClick = {
                            navController.navigate(
                                Rutas.Movimiento.ruta
                            ) {
                                popUpTo(Rutas.Catalogo.ruta)
                                launchSingleTop = true
                            }
                        },
                        icon = { Text("↕") },
                        label = { Text("Movimientos") }
                    )

                    NavigationBarItem(
                        selected =
                            rutaActual == Rutas.Alertas.ruta,
                        onClick = {
                            navController.navigate(
                                Rutas.Alertas.ruta
                            ) {
                                popUpTo(Rutas.Catalogo.ruta)
                                launchSingleTop = true
                            }
                        },
                        icon = { Text("!") },
                        label = { Text("Alertas") }
                    )

                    if (usuarioActivo?.rol == Rol.ADMINISTRADOR) {

                        NavigationBarItem(
                            selected =
                                rutaActual == Rutas.AgregarProducto.ruta,
                            onClick = {
                                navController.navigate(
                                    Rutas.AgregarProducto.ruta
                                ) {
                                    popUpTo(Rutas.Catalogo.ruta)
                                    launchSingleTop = true
                                }
                            },
                            icon = { Text("+") },
                            label = { Text("Agregar") }
                        )
                    }
                }
            }
        }

    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Rutas.Login.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Rutas.Login.ruta) {

                PantallaLogin(
                    viewModel = sesionViewModel,
                    onLoginCorrecto = {
                        navController.navigate(
                            Rutas.Catalogo.ruta
                        ) {
                            popUpTo(Rutas.Login.ruta) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Rutas.Catalogo.ruta) {

                AccesoRestringido(
                    permitido = usuarioActivo != null,
                    onDenegado = {
                        navController.navigate(Rutas.Login.ruta)
                    }
                ) {
                    CatalogoScreen(
                        viewModel = inventarioViewModel,
                        onProductoClick = { codigo ->
                            navController.navigate(
                                Rutas.DetalleProducto.crearRuta(codigo)
                            )
                        }
                    )
                }
            }

            composable(
                route = Rutas.DetalleProducto.ruta,
                arguments = listOf(
                    navArgument("codigo") {
                        type = NavType.StringType
                    }
                )
            ) { entrada ->

                val codigo =
                    entrada.arguments?.getString("codigo") ?: ""

                AccesoRestringido(
                    permitido = usuarioActivo != null,
                    onDenegado = { irCatalogo() }
                ) {
                    PantallaDetalleProducto(
                        codigo = codigo,
                        viewModel = inventarioViewModel
                    )
                }
            }

            composable(Rutas.AgregarProducto.ruta) {

                AccesoRestringido(
                    permitido =
                        usuarioActivo?.rol == Rol.ADMINISTRADOR,
                    onDenegado = { irCatalogo() }
                ) {
                    FormularioProductoScreen(
                        viewModel = inventarioViewModel,
                        onProductoGuardado = {
                            irCatalogo()
                        }
                    )
                }
            }

            composable(Rutas.Movimiento.ruta) {

                AccesoRestringido(
                    permitido = usuarioActivo != null,
                    onDenegado = { irCatalogo() }
                ) {
                    PantallaMovimiento()
                }
            }

            composable(Rutas.Alertas.ruta) {

                AccesoRestringido(
                    permitido = usuarioActivo != null,
                    onDenegado = { irCatalogo() }
                ) {
                    PantallaAlertas(
                        viewModel = inventarioViewModel,
                        onProductoClick = { codigo ->
                            navController.navigate(
                                Rutas.DetalleProducto.crearRuta(codigo)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AccesoRestringido(
    permitido: Boolean,
    onDenegado: () -> Unit,
    contenido: @Composable () -> Unit
) {
    LaunchedEffect(permitido) {
        if (!permitido) {
            onDenegado()
        }
    }

    if (permitido) {
        contenido()
    }
}
