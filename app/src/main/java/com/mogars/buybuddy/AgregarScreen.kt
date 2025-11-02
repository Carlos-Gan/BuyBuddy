package com.mogars.buybuddy

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.Save

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarScreen(
    nombre: String? = "",
    onBackClick: () -> Unit = {},
) {
    var productName by rememberSaveable { mutableStateOf(nombre ?: "") }
    var productMarca by rememberSaveable { mutableStateOf("") }
    var productPrecio by rememberSaveable { mutableStateOf("") }

    // Validación mejorada
    val camposValidos = productName.isNotBlank() &&
            productMarca.isNotBlank() &&
            productPrecio.isNotBlank() &&
            productPrecio.toDoubleOrNull() != null

    // Animación de aparición
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.agregarProducto),
                        color = colorResource(R.color.white),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.ArrowLeft,
                            contentDescription = "Volver",
                            tint = colorResource(R.color.white)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* Acción de guardar */ },
                        enabled = camposValidos
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Save,
                            contentDescription = "Guardar",
                            tint = if (camposValidos)
                                colorResource(R.color.white)
                            else
                                colorResource(android.R.color.darker_gray)
                        )
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(R.color.principal),
                    scrolledContainerColor = colorResource(R.color.principal)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header Card
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.principal).copy(alpha = 0.08f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Nuevo Producto",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.principal)
                            )
                            Text(
                                text = "Completa la información del producto que deseas agregar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Formulario
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, delayMillis = 200)) +
                            slideInVertically(tween(600, delayMillis = 200))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Campo Nombre
                            FormField(
                                label = stringResource(R.string.nombre),
                                isRequired = true
                            ) {
                                TextInputModificado(
                                    valor = productName,
                                    label = stringResource(R.string.nombre),
                                    placeholder = stringResource(R.string.ingresarNom),
                                    teclado = KeyboardType.Text,
                                    onValueChange = { productName = it },
                                    isError = productName.isEmpty()
                                )
                            }

                            // Campo Marca
                            FormField(
                                label = stringResource(R.string.marca),
                                isRequired = true
                            ) {
                                TextInputModificado(
                                    valor = productMarca,
                                    label = stringResource(R.string.marca),
                                    placeholder = stringResource(R.string.ingresarMarca),
                                    teclado = KeyboardType.Text,
                                    onValueChange = { productMarca = it },
                                    isError = productMarca.isEmpty()
                                )
                            }

                            // Campo Precio
                            FormField(
                                label = stringResource(R.string.precio),
                                isRequired = true
                            ) {
                                TextInputModificado(
                                    valor = productPrecio,
                                    label = stringResource(R.string.precio),
                                    placeholder = stringResource(R.string.ingresarPrecio),
                                    teclado = KeyboardType.Decimal,
                                    onValueChange = { productPrecio = it },
                                    isError = productPrecio.isNotEmpty() && productPrecio.toDoubleOrNull() == null,
                                    prefix = "$"
                                )
                            }
                        }
                    }
                }

                // Indicador de progreso
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, delayMillis = 400))
                ) {
                    ProgressIndicator(
                        fieldsCompleted = listOf(
                            productName.isNotBlank(),
                            productMarca.isNotBlank(),
                            camposValidos
                        ).count { it },
                        totalFields = 3
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    isRequired: Boolean = false,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (isRequired) {
                Text(
                    text = "*",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        content()
    }
}

@Composable
fun TextInputModificado(
    valor: String,
    label: String,
    placeholder: String,
    teclado: KeyboardType,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    prefix: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
        prefix = prefix?.let { { Text(it, style = MaterialTheme.typography.bodyLarge) } },
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = Modifier
            .fillMaxWidth()
            .focusable(true),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else colorResource(R.color.principal),
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else colorResource(R.color.gris_claro),
            cursorColor = colorResource(R.color.principal),
            focusedLabelColor = if (isError) MaterialTheme.colorScheme.error else colorResource(R.color.principal),
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedContainerColor = colorResource(R.color.principal).copy(alpha = 0.02f),
            unfocusedContainerColor = Color.Transparent
        ),
        isError = isError,
        supportingText = if (isError && teclado == KeyboardType.Decimal) {
            { Text("Ingresa un precio válido", color = MaterialTheme.colorScheme.error) }
        } else null
    )
}

@Composable
fun ProgressIndicator(
    fieldsCompleted: Int,
    totalFields: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.principal).copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso del formulario",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$fieldsCompleted/$totalFields",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.principal)
                )
            }

            LinearProgressIndicator(
                progress = { fieldsCompleted.toFloat() / totalFields },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = colorResource(R.color.principal),
                trackColor = colorResource(R.color.gris_claro).copy(alpha = 0.3f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AgregarScreenPreview() {
    MaterialTheme {
        AgregarScreen(nombre = "Frijol")
    }
}