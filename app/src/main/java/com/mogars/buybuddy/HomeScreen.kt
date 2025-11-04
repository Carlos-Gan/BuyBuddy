package com.mogars.buybuddy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Trash
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.stringResource
import com.mogars.buybuddy.ViewModels.HomeViewModel
import kotlinx.coroutines.delay

/**
 * Pantalla principal con lista de compras tipo checklist
 * Los productos marcados como completados se mueven al final
 * Después del tiempo configurado, se desactivan automáticamente (estado = false)
 */
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    // Obtener la lista de productos activos (solo con estado = true)
    val productos by viewModel.productosActivos.collectAsState(initial = emptyList())

    // Obtener el estado de completados del ViewModel
    val productosCompletados by viewModel.productosCompletados.collectAsState(initial = emptySet())

    // Obtener preferencias de limpieza automática
    val limpiarAuto by viewModel.limpiarCompletadosAuto.collectAsState(initial = false)
    val tiempoLimpieza by viewModel.tiempoLimpiezaMinutos.collectAsState(initial = 5)

    // Rastrear cuándo se completó cada producto
    var tiemposComplecion by remember { mutableStateOf(mapOf<Int, Long>()) }

    // Efecto para cambiar estado automáticamente después del tiempo
    LaunchedEffect(limpiarAuto, tiempoLimpieza, productosCompletados) {
        if (limpiarAuto && tiempoLimpieza > 0) {
            while (true) {
                delay(1000) // Verificar cada segundo

                val ahora = System.currentTimeMillis()
                val tiempoLimpiezaMs = tiempoLimpieza * 60 * 1000L

                // Encontrar productos completados que deben ser desactivados
                val productosADesactivar = productosCompletados.filter { productoId ->
                    tiemposComplecion.containsKey(productoId) && // Existe en el mapa
                            (ahora - tiemposComplecion[productoId]!!) >= tiempoLimpiezaMs // Pasó el tiempo
                }

                // Desactivar productos (cambiar estado a false en la BD)
                productosADesactivar.forEach { productoId ->
                    viewModel.actualizarEstadoProducto(productoId, false)
                    // Remover del mapa de tiempos
                    tiemposComplecion = tiemposComplecion - productoId
                }
            }
        }
    }

    // Separar productos completados de incompletos
    val productosIncompletos = productos.filter { it.id !in productosCompletados }
    val productosCompletadosList = productos.filter { it.id in productosCompletados }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
            .paddingFromBaseline(top = 35.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        HeaderProductos(
            totalProductos = productos.size,
            completados = productosCompletados.size
        )

        // Lista de productos o estado vacío
        if (productos.isEmpty()) {
            EmptyProductosState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                // Productos incompletos primero
                items(productosIncompletos) { producto ->
                    ProductoChecklistCard(
                        producto = producto,
                        isCompleted = false,
                        onToggleComplete = {
                            viewModel.marcarComoCompletado(producto.id)
                            // Registrar el tiempo de compleción
                            tiemposComplecion = tiemposComplecion + (producto.id to System.currentTimeMillis())
                        },
                        onEliminar = { viewModel.eliminarProducto(producto.id) }
                    )
                }

                // Separador si hay completados
                if (productosCompletadosList.isNotEmpty()) {
                    item {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth(),
                            color = colorResource(R.color.gris_claro)
                        )
                        Text(stringResource(R.string.productosCompletados))
                    }
                }

                // Productos completados al final
                items(productosCompletadosList) { producto ->
                    ProductoChecklistCard(
                        producto = producto,
                        isCompleted = true,
                        onToggleComplete = {
                            viewModel.marcarComoNoCompletado(producto.id)
                            // Remover del mapa de tiempos
                            tiemposComplecion = tiemposComplecion - producto.id
                        },
                        onEliminar = {
                            viewModel.eliminarProducto(producto.id)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Header mejorado con contador de completados
 */
@Composable
fun HeaderProductos(totalProductos: Int = 0, completados: Int = 0) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.listaDeCompras),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.principal)
        )
        Text(
            text = "$completados ${stringResource(R.string.de)} $totalProductos ${stringResource(R.string.completados)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Tarjeta de producto con checkbox
 */
@Composable
fun ProductoChecklistCard(
    producto: Producto,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                colorResource(R.color.gris_claro).copy(alpha = 0.2f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete() },
                modifier = Modifier.size(24.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = colorResource(R.color.principal),
                    uncheckedColor = colorResource(R.color.principal)
                )
            )

            // Información del producto
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Nombre del producto
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                // Marca y presentación
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.marca)}: ${producto.marca}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = producto.presentacion
                            ?: "${producto.cantidad} ${producto.unidadMetrica}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorResource(R.color.principal),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Botón eliminar
            IconButton(
                onClick = onEliminar,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Trash,
                    contentDescription = stringResource(R.string.eliminar),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Estado vacío cuando no hay productos
 */
@Composable
fun EmptyProductosState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "🛒",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = stringResource(R.string.sinProductos),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.agregarProductoParaComenzar),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}