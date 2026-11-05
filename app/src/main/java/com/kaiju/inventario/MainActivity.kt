
package com.kaiju.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.kaiju.inventario.navigation.AppNavHost
import com.kaiju.inventario.ui.theme.KaijuAppTheme
import com.kaiju.inventario.viewmodel.InventarioViewModel
import com.kaiju.inventario.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val inventarioViewModel = ViewModelProvider(this)[
            InventarioViewModel::class.java
        ]

        val sesionViewModel = ViewModelProvider(this)[
            SesionViewModel::class.java
        ]

        setContent {
            KaijuAppTheme {
                AppNavHost(
                    inventarioViewModel = inventarioViewModel,
                    sesionViewModel = sesionViewModel
                )
            }
        }
    }
}
