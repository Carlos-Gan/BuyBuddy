package com.mogars.buybuddy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.navigation.NavHostController
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Trash
import java.time.LocalDate


//Metodos utilizados
fun Producto.precioActual(): Float? = precios.maxByOrNull { it.fecha }?.costo


@Composable
fun BuscarScreen(
    navController: NavHostController,
    onResultClick: (Producto) -> Unit = {},
) {
    val textFieldState = rememberTextFieldState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }

    // Lista de productos simulada
    val allProductos = remember {
        listOf(
            Producto(
                id = 1,
                nombre = "Leche",
                marca = "Lala",
                estado = false,
                precios = listOf(
                    Precio(18.50f, LocalDate.of(2025, 10, 1)),
                    Precio(32.50f, LocalDate.of(2025, 10, 10))
                )
            ),
            Producto(
                id = 2,
                nombre = "Pan Artesanal",
                marca = "Bimbo",
                estado = false,
                precios = listOf(
                    Precio(18.50f, LocalDate.of(2025, 10, 1)),
                    Precio(32.50f, LocalDate.of(2025, 10, 10))
                )
            ),
            Producto(
                id = 3,
                nombre = "Queso Oaxaca",
                marca = "Generico",
                estado = false,
                precios = listOf(
                    Precio(18.50f, LocalDate.of(2025, 10, 8)),
                )
            )
        ).sortedBy { it.nombre }
    }

    val query = textFieldState.text.toString()

    val filteredProductos by remember(query) {
        mutableStateOf(
            if (query.isBlank()) allProductos
            else allProductos.filter { it.nombre.contains(query, ignoreCase = true) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Text(
            text = stringResource(R.string.buscar),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp, bottom = 16.dp)
        )

        SearchBarSimple(textFieldState)

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (query.isBlank() || filteredProductos.isNotEmpty()) {
                items(filteredProductos) { producto ->
                    ResultadoItem(
                        producto = producto,
                        onClick = { onResultClick(producto) }
                    )
                }
            } else { // No hay coincidencias
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
    if (showDialog && selectedProducto != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.opciones)) },
            text = {
                Text(text = stringResource(R.string.accion1_op, selectedProducto?.nombre ?: ""))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        navController.navigate("")
                    }
                ) { Text(stringResource(R.string.modificar)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        navController.navigate("")
                    }
                ) { Text(stringResource(R.string.agregar)) }
            }
        )
    }
}

@Composable
fun SearchBarSimple(textFieldState: TextFieldState) {
    TextField(
        value = textFieldState.text.toString(),
        onValueChange = { textFieldState.edit { replace(0, length, it) } },
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
                    modifier = Modifier
                        .size(15.dp),
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
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
            )
            {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = producto.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = producto.marca,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f),
                    color = colorResource(R.color.gris_claro)
                )
            }
            Text(
                text = producto.precioActual().toString(),
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

