package com.mogars.buybuddy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "precios")
data class PrecioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,
    val costo: Float,
    val fecha: String,
    val tienda: String? = null,
    val notas: String? = null
)
