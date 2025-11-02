package com.mogars.buybuddy.data.repository

import com.mogars.buybuddy.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.mogars.buybuddy.data.local.dao.ProductoDao
import com.mogars.buybuddy.data.local.dao.PrecioDao
import com.mogars.buybuddy.data.local.entity.ProductoEntity
import com.mogars.buybuddy.data.local.entity.PrecioEntity
import java.time.LocalDate

class ProductoRepository(
    private val productoDao: ProductoDao,
    private val precioDao: PrecioDao
) {
    // Guardar nuevo producto
    suspend fun guardarProducto(
        nombre: String,
        marca: String,
        cantidad: Float,
        unidadMetrica: String,
        presentacion: String,
        precioActual: Float
    ) {
        val producto = ProductoEntity(
            nombre = nombre,
            marca = marca,
            cantidad = cantidad,
            unidadMetrica = unidadMetrica,
            presentacion = presentacion,
            precioActual = precioActual
        )
        val productoId = productoDao.insertarProducto(producto).toInt()

        // Guardar el precio inicial
        val precio = PrecioEntity(
            productoId = productoId,
            costo = precioActual,
            fecha = LocalDate.now().toString()
        )
        precioDao.insertarPrecio(precio)
    }

    // Obtener todos los productos
    fun obtenerTodosLosProductos(): Flow<List<Producto>> {
        return productoDao.obtenerTodosLosProductos().map { productos ->
            productos.map { it.toProducto() }
        }
    }

    // Buscar productos
    fun buscarProductos(nombre: String): Flow<List<Producto>> {
        return productoDao.buscarProductosPorNombre(nombre).map { productos ->
            productos.map { it.toProducto() }
        }
    }

    // Obtener producto por ID
    suspend fun obtenerProductoPorId(id: Int): Producto? {
        val productoEntity = productoDao.obtenerProductoPorId(id) ?: return null
        val precios = precioDao.obtenerPreciosPorProducto(id)
        return productoEntity.toProducto()
    }

    // Actualizar producto
    suspend fun actualizarProducto(
        id: Int,
        nombre: String,
        marca: String,
        cantidad: Float,
        unidadMetrica: String,
        presentacion: String,
        precioActual: Float
    ) {
        val producto = ProductoEntity(
            id = id,
            nombre = nombre,
            marca = marca,
            cantidad = cantidad,
            unidadMetrica = unidadMetrica,
            presentacion = presentacion,
            precioActual = precioActual,
            fechaActualizacion = LocalDate.now().toString()
        )
        productoDao.actualizarProducto(producto)

        // Guardar nuevo precio
        val precio = PrecioEntity(
            productoId = id,
            costo = precioActual,
            fecha = LocalDate.now().toString()
        )
        precioDao.insertarPrecio(precio)
    }

    // Eliminar producto
    suspend fun eliminarProducto(id: Int) {
        productoDao.eliminarProductoPorId(id)
    }

    // Conversor
    private fun ProductoEntity.toProducto(): Producto {
        return Producto(
            id = id,
            nombre = nombre,
            marca = marca,
            cantidad = cantidad,
            unidadMetrica = unidadMetrica,
            presentacion = presentacion,
            estado = estado,
            precios = emptyList()
        )
    }
    suspend fun actualizarEstadoProducto(id: Int, estado: Boolean) {
        productoDao.actualizarEstadoProducto(id, estado)
    }

    fun obtenerProductosActivos(): Flow<List<Producto>> {
        return productoDao.obtenerProductosActivos().map { productos ->
            productos.map { it.toProducto() }
        }
    }
}