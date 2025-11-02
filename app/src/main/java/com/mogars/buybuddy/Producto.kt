package com.mogars.buybuddy

import java.time.LocalDate

data class Producto(
    val id: Int,
    val nombre: String,
    val marca: String,
    val estado: Boolean,
    val precios: List<Precio> = emptyList(),

    //Cantidad y presentacion
    val cantidad: Float = 0f,               //Valor numero de la cantidad, ej: 1, 300, .300
    val unidadMetrica: String = "Unidad",   //Mililitros, Kilogramo, Gramo, Paquete
    val presentacion: String? = null,       //Descripcion, ejemplo: 300ml, 1kg, 6 unidades

    //Categorizacion
    val categorias: List<Categorias> = emptyList(),

    //Cosas posibles a implementar
    //val codigoBarras: String? = null,
    //val fotoProducto: List<String> = emptyList(),
    //val calificacion: Float? = null,
    //val notas: String? = null,
    )

data class Precio(
    val costo: Float,
    val fecha: LocalDate,
)

data class Categorias(
    val categoria: String? = null,
    val subcategoria: String? = null,
    val etiquetas: List<String> = emptyList(),
)
