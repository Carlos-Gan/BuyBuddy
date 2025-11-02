package com.mogars.buybuddy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Edit
import compose.icons.fontawesomeicons.solid.Trash
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.mogars.buybuddy.ViewModels.HomeViewModel

/**
 * Pantalla principal que muestra todos los productos activos
 *
 * @param viewModel ViewModel que maneja la lógica de HomeScreen (recibido desde MainActivity)
 */
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    // Obtener la lista de productos activos como State
    val productos by viewModel.productosActivos.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
            .paddingFromBaseline(top = 35.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        HeaderProductos()

        // Lista de productos o estado vacío
        if (productos.isEmpty()) {
            EmptyProductosState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(productos) { producto ->
                    ProductoCard(
                        producto = producto,
                        onEliminar = { viewModel.eliminarProducto(producto.id) },
                        onEditar = { /* Navegar a editar en el futuro */ }
                    )
                }
            }
        }
    }
}

/**
 * Header con título y descripción
 */
@Composable
fun HeaderProductos() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Mis Productos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.principal)
        )
        Text(
            text = "Gestiona tu lista de compras",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Tarjeta individual de producto con información y acciones
 *
 * @param producto Datos del producto a mostrar
 * @param onEliminar Callback cuando se presiona el botón eliminar
 * @param onEditar Callback cuando se presiona el botón editar
 */
@Composable
fun ProductoCard(
    producto: Producto,
    onEliminar: () -> Unit,
    onEditar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sección izquierda: Información del producto
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Nombre del producto
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Marca y presentación
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Marca: ${producto.marca}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = producto.presentacion ?: "${producto.cantidad} ${producto.unidadMetrica}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorResource(R.color.principal),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Badge de cantidad
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp)),
                    color = colorResource(R.color.principal).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${producto.cantidad} ${producto.unidadMetrica}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = colorResource(R.color.principal),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Sección derecha: Botones de acciones
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón editar
                IconButton(
                    onClick = onEditar,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Edit,
                        contentDescription = "Editar",
                        tint = colorResource(R.color.principal),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Botón eliminar
                IconButton(
                    onClick = onEliminar,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Trash,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Estado vacío cuando no hay productos agregados
 */
@Composable
fun EmptyProductosState() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Emoji de portapapeles
            Text(
                text = "📋",
                style = MaterialTheme.typography.displayLarge
            )

            // Título
            Text(
                text = "Sin productos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Descripción
            Text(
                text = "Agrega tu primer producto para comenzar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}