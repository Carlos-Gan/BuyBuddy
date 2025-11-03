package com.mogars.buybuddy.data.repository

import com.mogars.buybuddy.Producto
import com.mogars.buybuddy.Precio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.mogars.buybuddy.data.local.dao.ProductoDao
import com.mogars.buybuddy.data.local.dao.PrecioDao
import com.mogars.buybuddy.data.local.entity.ProductoEntity
import com.mogars.buybuddy.data.local.entity.PrecioEntity
import com.mogars.buybuddy.data.local.entity.ProductoConPrecios
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

    // Obtiene productos CON precios
    fun obtenerTodosLosProductos(): Flow<List<Producto>> {
        return productoDao.obtenerTodosLosProductos().map { productosConPrecios ->
            productosConPrecios.map { it.aProducto() }
        }
    }

    // Búsqueda CON precios
    fun buscarProductos(nombre: String): Flow<List<Producto>> {
        return productoDao.buscarProductosPorNombre(nombre).map { productosConPrecios ->
            productosConPrecios.map { it.aProducto() }
        }
    }

    // Obtiene producto CON precios
    suspend fun obtenerProductoPorId(id: Int): Producto? {
        return productoDao.obtenerProductoPorIdConPrecios(id)?.aProducto()
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

    // Marcar como completado (estado = false)
    suspend fun actualizarEstadoProducto(id: Int, estado: Boolean) {
        productoDao.actualizarEstadoProducto(id, estado)
    }

    // Obtiene productos activos con precios
    fun obtenerProductosActivos(): Flow<List<Producto>> {
        return productoDao.obtenerProductosActivos().map { productosConPrecios ->
            productosConPrecios.map { it.aProducto() }
        }
    }

    // ==================== MAPEO DE ENTIDADES ====================

    private fun ProductoConPrecios.aProducto(): Producto {
        return Producto(
            id = producto.id,
            nombre = producto.nombre,
            marca = producto.marca,
            cantidad = producto.cantidad,
            unidadMetrica = producto.unidadMetrica,
            presentacion = producto.presentacion,
            estado = producto.estado,
            // Convertir lista de precios
            precios = precios.map { precioEntity ->
                Precio(
                    costo = precioEntity.costo,
                    fecha = LocalDate.parse(precioEntity.fecha)
                )
            },
            categorias = emptyList()
        )
    }
}