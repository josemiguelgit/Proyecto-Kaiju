
package com.kaiju.inventario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaMovimiento() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Movimientos de inventario",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            "Aquí registraremos entradas, salidas por venta, " +
                    "salidas por consumo y ajustes."
        )
    }
}
