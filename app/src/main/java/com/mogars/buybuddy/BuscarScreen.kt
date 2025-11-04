package com.mogars.buybuddy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mogars.buybuddy.viewModel.BuscarViewModel
import com.mogars.buybuddy.viewModel.BuscarViewModelFactory
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Trash
import com.mogars.buybuddy.data.repository.ProductoRepository


// Método para obtener el precio actual de un producto
fun Producto.precioActual(): Float? {
    return if (precios.isNotEmpty()) {
        precios.maxByOrNull { it.fecha }?.costo
    } else {
        null
    }
}

// Método para calcular el precio unitario basado en la cantidad y unidad métrica
fun Producto.precioUnitario(): Float? {
    val precioBase = precioActual() ?: return null
    if (precioBase <= 0) return null

    val cant = if (cantidad > 0) cantidad else 1f

    return when (unidadMetrica.lowercase().trim()) {
        "kg", "kilogramo", "kilogramos" -> precioBase / cant
        "l", "litro", "litros" -> precioBase / cant
        "ml", "mililitro", "mililitros" -> precioBase / cant
        "g", "gramo", "gramos" -> precioBase / cant
        "unidad", "unidades", "u", "und" -> precioBase / cant
        "docena", "docenas" -> precioBase / (cant * 12)
        "paquete", "paquetes", "paq" -> precioBase / cant
        else -> precioBase / cant
    }
}

// Método para obtener el precio formateado con la unidad métrica
fun Producto.precioFormato(): String {
    // Si no hay precios, intentar mostrar al menos el precio base
    val precioBase = precioActual()
    if (precioBase == null || precioBase <= 0) {
        return "N/A"
    }

    val precio = precioUnitario() ?: precioBase
    val unidad = when (unidadMetrica.lowercase().trim()) {
        "kg", "kilogramo", "kilogramos" -> "/kg"
        "l", "litro", "litros" -> "/L"
        "ml", "mililitro", "mililitros" -> "/mL"
        "g", "gramo", "gramos" -> "/g"
        "unidad", "unidades", "u", "und" -> "/u"
        "docena", "docenas" -> "/doc"
        "paquete", "paquetes", "paq" -> "/paq"
        else -> ""
    }
    return "${"%.2f".format(precio)}$unidad"
}

@Composable
fun BuscarScreen(
    navController: NavHostController,
    repository: ProductoRepository
) {
    // Crear ViewModel
    val viewModel: BuscarViewModel = viewModel(
        factory = BuscarViewModelFactory(repository)
    )

    val textFieldState = rememberTextFieldState()
    val query = textFieldState.text.toString()

    // Obtener productos filtrados del ViewModel
    val filteredProductos by viewModel.buscarProductos(query).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        // Título
        Text(
            text = stringResource(R.string.buscar),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp, bottom = 16.dp)
        )

        // Barra de búsqueda
        SearchBarSimple(textFieldState)

        Spacer(modifier = Modifier.height(20.dp))

        // Lista de resultados
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (query.isBlank()) {
                // Si el query está vacío, mostrar todos los productos
                items(filteredProductos) { producto ->
                    ResultadoItem(
                        producto = producto,
                        onClick = { /* Acción al hacer click */ }
                    )
                }
            } else if (filteredProductos.isNotEmpty()) {
                // Si hay coincidencias, mostrar productos filtrados
                items(filteredProductos) { producto ->
                    ResultadoItem(
                        producto = producto,
                        onClick = { /* Acción al hacer click */ }
                    )
                }
            } else {
                // Si no hay coincidencias, mostrar botón para agregar nuevo producto
                item {
                    ResultadoItemAdd(
                        text = "${stringResource(R.string.agregar)} \"$query\"",
                        onClick = {
                            navController.navigate("AgregarScreen?nombre=$query")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBarSimple(textFieldState: TextFieldState) {
    TextField(
        value = textFieldState.text.toString(),
        onValueChange = { newValue ->
            textFieldState.edit { replace(0, length, newValue) }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp)),
        placeholder = {
            Text(
                "${stringResource(R.string.busca)}...",
                color = colorResource(R.color.white)
            )
        },
        leadingIcon = {
            Icon(
                FontAwesomeIcons.Solid.Search,
                contentDescription = stringResource(R.string.busca),
                modifier = Modifier.size(20.dp),
                tint = colorResource(R.color.white)
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorResource(R.color.secundario),
            unfocusedContainerColor = colorResource(R.color.secundario),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            IconButton(
                onClick = {
                    textFieldState.edit { replace(0, length, "") }
                }
            ) {
                Icon(
                    FontAwesomeIcons.Solid.Trash,
                    contentDescription = stringResource(R.string.borrar),
                    modifier = Modifier.size(15.dp),
                    tint = colorResource(R.color.white)
                )
            }
        }
    )
}

@Composable
fun ResultadoItem(
    producto: Producto,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(62.dp)
            .clickable { onClick() }
            .clip(RoundedCornerShape(18.dp))
            .background(colorResource(R.color.principal))
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información del producto (izquierda)
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Nombre
                Text(
                    text = producto.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    maxLines = 1
                )

                // Marca
                Text(
                    text = producto.marca,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    color = colorResource(R.color.gris_claro),
                    maxLines = 1
                )
            }

            // Precio unitario formateado (derecha)
            Text(
                text = producto.precioFormato(),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.gris_claro)
            )
        }
    }
}

@Composable
fun ResultadoItemAdd(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .clip(RoundedCornerShape(18.dp))
            .background(colorResource(R.color.principal))
            .padding(16.dp)
    ) {
        Text(
            text = " $text ",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.white)
        )
    }
}