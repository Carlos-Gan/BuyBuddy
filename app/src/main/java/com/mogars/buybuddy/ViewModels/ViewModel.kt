package com.mogars.buybuddy.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.mogars.buybuddy.data.repository.ProductoRepository

class HomeViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    val productosActivos = repository.obtenerProductosActivos()

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            repository.eliminarProducto(id)
        }
    }
    fun actualizarEstadoProducto(id: Int, estado: Boolean) {
        viewModelScope.launch {
            repository.actualizarEstadoProducto(id, estado)
        }
    }
}

class HomeViewModelFactory(
    private val repository: ProductoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T  //
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
