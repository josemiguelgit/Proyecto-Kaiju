package com.kaiju.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.kaiju.inventario.data.local.KaijuDatabase
import com.kaiju.inventario.navigation.AppNavHost
import com.kaiju.inventario.repository.InventarioRepository
import com.kaiju.inventario.ui.theme.KaijuAppTheme
import com.kaiju.inventario.viewmodel.InventarioViewModel
import com.kaiju.inventario.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val database = KaijuDatabase.getInstance(
            applicationContext
        )

        val repository = InventarioRepository(database)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {

                if (modelClass.isAssignableFrom(
                        InventarioViewModel::class.java
                    )
                ) {
                    @Suppress("UNCHECKED_CAST")
                    return InventarioViewModel(repository) as T
                }

                throw IllegalArgumentException(
                    "ViewModel desconocido: ${modelClass.name}"
                )
            }
        }

        val inventarioViewModel = ViewModelProvider(
            this,
            factory
        )[InventarioViewModel::class.java]

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
