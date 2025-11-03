package com.mogars.buybuddy.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mogars.buybuddy.data.PreferencesManager
import kotlinx.coroutines.launch
import com.mogars.buybuddy.data.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow

class HomeViewModel(
    private val repository: ProductoRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val productosActivos = repository.obtenerProductosActivos()

    // ✅ Obtener directamente del PreferencesManager (persiste en BD)
    val productosCompletados: Flow<Set<Int>> = preferencesManager.obtenerProductosCompletados()

    fun marcarComoCompletado(id: Int) {
        viewModelScope.launch {
            preferencesManager.marcarComoCompletado(id)
        }
    }

    fun marcarComoNoCompletado(id: Int) {
        viewModelScope.launch {
            preferencesManager.marcarComoNoCompletado(id)
        }
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            repository.eliminarProducto(id)
            // También eliminarlo de los completados
            preferencesManager.marcarComoNoCompletado(id)
        }
    }

    fun actualizarEstadoProducto(id: Int, estado: Boolean) {
        viewModelScope.launch {
            repository.actualizarEstadoProducto(id, estado)
        }
    }

    fun limpiarCompletados() {
        viewModelScope.launch {
            preferencesManager.limpiarCompletados()
        }
    }
}

class HomeViewModelFactory(
    private val repository: ProductoRepository,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}