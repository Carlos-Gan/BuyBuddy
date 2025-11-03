package com.mogars.buybuddy.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductoConPrecios(
    @Embedded
    val producto: ProductoEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "productoId"
    )
    val precios: List<PrecioEntity> = emptyList()
)