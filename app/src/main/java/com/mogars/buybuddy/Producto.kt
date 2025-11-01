package com.mogars.buybuddy

import java.time.LocalDate

data class Producto(
    val id: Int,
    val nombre: String,
    val marca: String,
    val estado: Boolean,
    val precios: List<Precio> = emptyList()
)

data class Precio(
    val costo: Float,
    val fecha: LocalDate,
)