package com.mogars.buybuddy.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.mogars.buybuddy.data.local.entity.ProductoEntity
import com.mogars.buybuddy.data.local.entity.ProductoConPrecios
import com.mogars.buybuddy.data.local.entity.PrecioEntity

@Dao
interface ProductoDao {
    // Operaciones CRUD para Productos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: ProductoEntity): Long

    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    @Delete
    suspend fun eliminarProducto(producto: ProductoEntity)

    // ✅ NUEVA: Obtener producto CON precios
    @Transaction
    @Query("SELECT * FROM productos WHERE id = :productoId")
    suspend fun obtenerProductoPorIdConPrecios(productoId: Int): ProductoConPrecios?

    // ✅ NUEVA: Obtener producto sin precios (para queries rápidas)
    @Query("SELECT * FROM productos WHERE id = :productoId")
    suspend fun obtenerProductoPorId(productoId: Int): ProductoEntity?

    // ✅ ACTUALIZADA: Ahora retorna ProductoConPrecios
    @Transaction
    @Query("SELECT * FROM productos ORDER BY fechaCreacion DESC")
    fun obtenerTodosLosProductos(): Flow<List<ProductoConPrecios>>

    // ✅ ACTUALIZADA: Búsqueda CON precios
    @Transaction
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :nombre || '%' ORDER BY fechaCreacion DESC")
    fun buscarProductosPorNombre(nombre: String): Flow<List<ProductoConPrecios>>

    // ✅ ACTUALIZADA: Productos activos CON precios
    @Transaction
    @Query("SELECT * FROM productos WHERE estado = 1 ORDER BY fechaCreacion DESC")
    fun obtenerProductosActivos(): Flow<List<ProductoConPrecios>>

    @Query("DELETE FROM productos WHERE id = :productoId")
    suspend fun eliminarProductoPorId(productoId: Int)

    @Query("UPDATE productos SET estado = :estado WHERE id = :productoId")
    suspend fun actualizarEstadoProducto(productoId: Int, estado: Boolean)
}
