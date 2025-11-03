package com.mogars.buybuddy.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "buy_buddy_preferences")

        // Claves de preferencias
        private val PRODUCTOS_COMPLETADOS = stringSetPreferencesKey("productos_completados")
        private val OCULTAR_COMPLETADOS = booleanPreferencesKey("ocultar_completados")
        private val MOSTRAR_PRECIO_PROMEDIO = booleanPreferencesKey("mostrar_precio_promedio")
        private val ORDENAR_ALFABETICAMENTE = booleanPreferencesKey("ordenar_alfabeticamente")
    }

    // ========== PRODUCTOS COMPLETADOS ==========

    /**
     * Obtiene el flujo de IDs de productos completados
     */
    fun obtenerProductosCompletados(): Flow<Set<Int>> {
        return context.dataStore.data.map { preferences ->
            preferences[PRODUCTOS_COMPLETADOS]?.map { it.toInt() }?.toSet() ?: emptySet()
        }
    }

    /**
     * Marca un producto como completado
     */
    suspend fun marcarComoCompletado(productoId: Int) {
        context.dataStore.edit { preferences ->
            val completados = preferences[PRODUCTOS_COMPLETADOS]?.toMutableSet() ?: mutableSetOf()
            completados.add(productoId.toString())
            preferences[PRODUCTOS_COMPLETADOS] = completados
        }
    }

    /**
     * Desmarca un producto como completado
     */
    suspend fun marcarComoNoCompletado(productoId: Int) {
        context.dataStore.edit { preferences ->
            val completados = preferences[PRODUCTOS_COMPLETADOS]?.toMutableSet() ?: mutableSetOf()
            completados.remove(productoId.toString())
            preferences[PRODUCTOS_COMPLETADOS] = completados
        }
    }

    /**
     * Limpia todos los productos completados
     */
    suspend fun limpiarCompletados() {
        context.dataStore.edit { preferences ->
            preferences[PRODUCTOS_COMPLETADOS] = emptySet()
        }
    }

    // ========== PREFERENCIAS DE CONFIGURACIÓN ==========

    /**
     * Obtiene la preferencia: ocultar productos completados
     */
    fun obtenerOcultarCompletados(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[OCULTAR_COMPLETADOS] ?: true
        }
    }

    suspend fun guardarOcultarCompletados(valor: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[OCULTAR_COMPLETADOS] = valor
        }
    }

    /**
     * Obtiene la preferencia: mostrar precio promedio
     */
    fun obtenerMostrarPrecioPromedio(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[MOSTRAR_PRECIO_PROMEDIO] ?: false
        }
    }

    suspend fun guardarMostrarPrecioPromedio(valor: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MOSTRAR_PRECIO_PROMEDIO] = valor
        }
    }

    /**
     * Obtiene la preferencia: ordenar alfabéticamente
     */
    fun obtenerOrdenarAlfabeticamente(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[ORDENAR_ALFABETICAMENTE] ?: false
        }
    }

    suspend fun guardarOrdenarAlfabeticamente(valor: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ORDENAR_ALFABETICAMENTE] = valor
        }
    }
}