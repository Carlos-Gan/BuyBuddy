package com.mogars.buybuddy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val marca: String,
    val cantidad: Float,
    val unidadMetrica: String,
    val presentacion: String? = null,
    val estado: Boolean = true,
    val precioActual: Float,
    val fechaCreacion: String = LocalDate.now().toString(),
    val fechaActualizacion: String = LocalDate.now().toString()
)