
package com.kaiju.inventario.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre")
    fun observarProductos(): Flow<List<ProductoEntity>>

    @Query(
        "SELECT * FROM productos " +
                "WHERE codigo = :codigo COLLATE NOCASE LIMIT 1"
    )
    suspend fun buscarPorCodigo(codigo: String): ProductoEntity?

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarProductos(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(producto: ProductoEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarTodos(productos: List<ProductoEntity>)

    @Update
    suspend fun actualizar(producto: ProductoEntity): Int
}
