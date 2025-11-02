package com.mogars.buybuddy.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.mogars.buybuddy.data.repository.ProductoRepository

/**
 * ViewModel para la pantalla AgregarScreen
 * Maneja la lógica de guardar nuevos productos en la base de datos
 */
class AgregarViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    /**
     * Guarda un nuevo producto en la base de datos
     *
     * @param nombre Nombre del producto (ej: "Leche", "Pan", "Carne molida")
     * @param marca Marca del producto (ej: "Lala", "Bimbo", "Premium")
     * @param precio Precio actual del producto en pesos
     * @param cantidad Cantidad del producto (ej: 1, 300, 0.5)
     * @param unidad Unidad de medida (ej: "Kilogramo (kg)", "Mililitro (ml)", "Unidad")
     * @param presentacion Descripción personalizada del producto (ej: "1kg de carne molida")
     */
    fun guardarProducto(
        nombre: String,
        marca: String,
        precio: Float,
        cantidad: Float,
        unidad: String,
        presentacion: String
    ) {
        // Validar que los datos no estén vacíos
        if (nombre.isBlank() || marca.isBlank()) {
            return
        }

        // Validar que el precio y cantidad sean válidos
        if (precio <= 0f || cantidad <= 0f) {
            return
        }

        // Ejecutar en una corrutina para no bloquear el hilo principal
        viewModelScope.launch {
            try {
                repository.guardarProducto(
                    nombre = nombre.trim(),
                    marca = marca.trim(),
                    cantidad = cantidad,
                    unidadMetrica = unidad,
                    presentacion = presentacion.trim(),
                    precioActual = precio
                )
                // El producto se guardó exitosamente
                // La pantalla vuelve automáticamente en AgregarScreen
            } catch (e: Exception) {
                // Manejar errores de base de datos
                e.printStackTrace()
            }
        }
    }

    /**
     * Actualiza un producto existente en la base de datos
     *
     * @param id ID del producto a actualizar
     * @param nombre Nuevo nombre del producto
     * @param marca Nueva marca del producto
     * @param precio Nuevo precio del producto
     * @param cantidad Nueva cantidad
     * @param unidad Nueva unidad de medida
     * @param presentacion Nueva presentación
     */
    fun actualizarProducto(
        id: Int,
        nombre: String,
        marca: String,
        precio: Float,
        cantidad: Float,
        unidad: String,
        presentacion: String
    ) {
        // Validar datos
        if (nombre.isBlank() || marca.isBlank()) {
            return
        }

        if (precio <= 0f || cantidad <= 0f) {
            return
        }

        viewModelScope.launch {
            try {
                repository.actualizarProducto(
                    id = id,
                    nombre = nombre.trim(),
                    marca = marca.trim(),
                    cantidad = cantidad,
                    unidadMetrica = unidad,
                    presentacion = presentacion.trim(),
                    precioActual = precio
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

/**
 * Factory para crear instancias de AgregarViewModel
 * Necesario para pasar el Repository al ViewModel
 */
class AgregarViewModelFactory(
    private val repository: ProductoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgregarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgregarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}