package com.kaiju.inventario.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    fun observarMovimientos(): Flow<List<MovimientoEntity>>

    @Query(
        "SELECT * FROM movimientos " +
                "WHERE codigoProducto = :codigo ORDER BY fecha DESC"
    )
    fun observarPorProducto(codigo: String): Flow<List<MovimientoEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(movimiento: MovimientoEntity)
}
