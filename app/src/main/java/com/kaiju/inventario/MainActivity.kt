package com.kaiju.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kaiju.inventario.ui.screen.CatalogoScreen
import com.kaiju.inventario.ui.theme.KaijuAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            KaijuAppTheme {
                CatalogoScreen()
            }
        }
    }
}