package com.mogars.buybuddy.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mogars.buybuddy.Producto
import kotlinx.coroutines.flow.Flow
import com.mogars.buybuddy.data.repository.ProductoRepository

class BuscarViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    fun buscarProductos(query: String): Flow<List<Producto>> {
        return if (query.isBlank()) {
            repository.obtenerTodosLosProductos()
        } else {
            repository.buscarProductos(query)
        }
    }
}

class BuscarViewModelFactory(
    private val repository: ProductoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BuscarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BuscarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}