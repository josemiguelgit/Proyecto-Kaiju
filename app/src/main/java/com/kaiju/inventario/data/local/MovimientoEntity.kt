
package com.kaiju.inventario.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["codigo"],
            childColumns = ["codigoProducto"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index("codigoProducto")
    ]
)
data class MovimientoEntity(
    @PrimaryKey
    val id: String,
    val codigoProducto: String,
    val tipo: String,
    val cantidad: Int,
    val motivo: String?,
    val fecha: Long,
    val usuario: String,
    val sentidoAjuste: String?
)
