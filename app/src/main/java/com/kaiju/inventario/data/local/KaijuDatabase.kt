
package com.kaiju.inventario.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProductoEntity::class,
        MovimientoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KaijuDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    abstract fun movimientoDao(): MovimientoDao

    companion object {

        @Volatile
        private var INSTANCE: KaijuDatabase? = null

        fun getInstance(context: Context): KaijuDatabase {
            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KaijuDatabase::class.java,
                    "kaiju_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
