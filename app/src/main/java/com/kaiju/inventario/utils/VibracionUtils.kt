
package com.kaiju.inventario.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibracionUtils {

    @Suppress("DEPRECATION")
    fun vibrarError(context: Context) {

        val vibrator: Vibrator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                val manager = context.getSystemService(
                    VibratorManager::class.java
                )

                manager.defaultVibrator

            } else {

                context.getSystemService(
                    Context.VIBRATOR_SERVICE
                ) as Vibrator
            }

        if (!vibrator.hasVibrator()) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val efecto = VibrationEffect.createOneShot(
                150L,
                VibrationEffect.DEFAULT_AMPLITUDE
            )

            vibrator.vibrate(efecto)

        } else {

            vibrator.vibrate(150L)
        }
    }
}
