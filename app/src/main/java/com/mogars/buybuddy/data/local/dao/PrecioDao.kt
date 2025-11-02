package com.mogars.buybuddy.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.mogars.buybuddy.data.local.entity.PrecioEntity
@Dao
interface PrecioDao {
    // Operaciones CRUD para Precios
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPrecio(precio: PrecioEntity)

    @Query("SELECT * FROM precios WHERE productoId = :productoId ORDER BY fecha DESC")
    fun obtenerPreciosPorProducto(productoId: Int): Flow<List<PrecioEntity>>

    @Query("SELECT * FROM precios WHERE productoId = :productoId ORDER BY fecha DESC LIMIT 1")
    suspend fun obtenerPrecioMasReciente(productoId: Int): PrecioEntity?

    @Query("SELECT AVG(costo) FROM precios WHERE productoId = :productoId")
    suspend fun obtenerPromedioPrecio(productoId: Int): Float?

    @Delete
    suspend fun eliminarPrecio(precio: PrecioEntity)
}