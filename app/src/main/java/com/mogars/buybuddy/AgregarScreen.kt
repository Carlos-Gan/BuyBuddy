package com.mogars.buybuddy

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mogars.buybuddy.ViewModels.AgregarViewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.Plus
import compose.icons.fontawesomeicons.solid.Save

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AgregarScreen(
    nombre: String? = "",
    onBackClick: () -> Unit = {},
    viewModel: AgregarViewModel? = null
) {
    // Variables
    var productName by rememberSaveable { mutableStateOf(nombre ?: "") }
    var productMarca by rememberSaveable { mutableStateOf("") }
    var productPrecio by rememberSaveable { mutableStateOf("") }
    var productCantidad by rememberSaveable { mutableStateOf("") }
    var productUnidadMedida by rememberSaveable { mutableStateOf("Unidad") }
    var productPresentacion by rememberSaveable { mutableStateOf("") }
    var expandedToolbar by rememberSaveable { mutableStateOf(true) }
    var expandedUnidad by rememberSaveable { mutableStateOf(false) }
    var guardandoProducto by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    val unidadesDisponibles = listOf(
        "Unidad",
        "Mililitro (ml)",
        "Litro (L)",
        "Gramo (g)",
        "Kilogramo (kg)",
        "Paquete",
        "Caja",
        "Docena"
    )

    // Función para extraer solo la unidad (sin paréntesis)
    fun obtenerUnidadLimpia(unidad: String): String {
        return when {
            unidad.contains("ml") -> "ml"
            unidad.contains("Litro") -> "L"
            unidad.contains("Gramo") -> "g"
            unidad.contains("Kilogramo") -> "kg"
            unidad.contains("Paquete") -> "Paquete"
            unidad.contains("Caja") -> "Caja"
            unidad.contains("Docena") -> "Docena"
            else -> "Unidad"
        }
    }

    // Validación mejorada
    val precioFloat = productPrecio.toFloatOrNull()
    val cantidadFloat = productCantidad.toFloatOrNull()

    val validacionesIndividuales = mapOf(
        "Nombre" to (productName.isNotBlank()),
        "Marca" to (productMarca.isNotBlank()),
        "Precio" to (productPrecio.isNotBlank() && precioFloat != null && precioFloat > 0),
        "Cantidad" to (productCantidad.isNotBlank() && cantidadFloat != null && cantidadFloat > 0)
    )

    val camposValidos = validacionesIndividuales.values.all { it }

    // Animación de aparición
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Floating Toolbar
            HorizontalFloatingToolbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -FloatingToolbarDefaults.ScreenOffset)
                    .zIndex(1f),
                expanded = expandedToolbar,
                trailingContent = {
                    AppBarRow {
                        clickableItem(
                            onClick = onBackClick,
                            icon = {
                                Icon(
                                    FontAwesomeIcons.Solid.ArrowLeft,
                                    contentDescription = "Volver",
                                    Modifier.size(35.dp)
                                )
                            },
                            label = "",
                            enabled = !guardandoProducto
                        )
                        clickableItem(
                            onClick = {
                                if (camposValidos && viewModel != null && !guardandoProducto) {
                                    guardandoProducto = true
                                    mensajeError = ""

                                    try {
                                        val unidadLimpia = obtenerUnidadLimpia(productUnidadMedida)

                                        Log.d("AgregarScreen", "Guardando producto:")
                                        Log.d("AgregarScreen", "  Nombre: $productName")
                                        Log.d("AgregarScreen", "  Marca: $productMarca")
                                        Log.d("AgregarScreen", "  Precio: $productPrecio (Float: $precioFloat)")
                                        Log.d("AgregarScreen", "  Cantidad: $productCantidad (Float: $cantidadFloat)")
                                        Log.d("AgregarScreen", "  Unidad: $productUnidadMedida -> $unidadLimpia")
                                        Log.d("AgregarScreen", "  Presentación: ${productPresentacion.ifEmpty { "$productCantidad $unidadLimpia" }}")

                                        viewModel.guardarProducto(
                                            nombre = productName,
                                            marca = productMarca,
                                            precio = precioFloat ?: 0f,
                                            cantidad = cantidadFloat ?: 0f,
                                            unidad = unidadLimpia,
                                            presentacion = productPresentacion.ifEmpty {
                                                "$productCantidad $unidadLimpia"
                                            }
                                        )

                                        // Esperar un momento antes de volver
                                        onBackClick()
                                    } catch (e: Exception) {
                                        mensajeError = "Error al guardar: ${e.message}"
                                        Log.e("AgregarScreen", "Error al guardar producto", e)
                                        guardandoProducto = false
                                    }
                                } else if (!camposValidos) {
                                    val camposFaltantes = validacionesIndividuales
                                        .filter { !it.value }
                                        .keys.joinToString(", ")
                                    mensajeError = "Completa: $camposFaltantes"
                                }
                            },
                            icon = {
                                if (guardandoProducto) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Icon(
                                        FontAwesomeIcons.Solid.Save,
                                        contentDescription = "Guardar",
                                        Modifier.size(35.dp),
                                        tint = if (camposValidos) Color.White else Color.Gray
                                    )
                                }
                            },
                            label = "",
                            enabled = camposValidos && !guardandoProducto
                        )
                    }
                },
                content = { },
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Mostrar mensaje de error si existe
                if (mensajeError.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = mensajeError,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

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
                                isRequired = true,
                                isValid = validacionesIndividuales["Nombre"] ?: false
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
                                isRequired = true,
                                isValid = validacionesIndividuales["Marca"] ?: false
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

                            // Campo Cantidad
                            FormField(
                                label = "Cantidad",
                                isRequired = true,
                                isValid = validacionesIndividuales["Cantidad"] ?: false
                            ) {
                                TextInputModificado(
                                    valor = productCantidad,
                                    label = "Cantidad",
                                    placeholder = "Ej: 1, 300, 0.5",
                                    teclado = KeyboardType.Decimal,
                                    onValueChange = { productCantidad = it },
                                    isError = productCantidad.isNotEmpty() && productCantidad.toFloatOrNull() == null
                                )
                            }

                            // Campo Unidad de Medida (Dropdown)
                            FormField(
                                label = "Unidad de Medida",
                                isRequired = true,
                                isValid = true
                            ) {
                                Box {
                                    OutlinedButton(
                                        onClick = { expandedUnidad = !expandedUnidad },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = colorResource(R.color.principal)
                                        )
                                    ) {
                                        Text(
                                            text = productUnidadMedida,
                                            modifier = Modifier.weight(1f),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                        )
                                        Icon(
                                            imageVector = FontAwesomeIcons.Solid.Plus,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expandedUnidad,
                                        onDismissRequest = { expandedUnidad = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .background(MaterialTheme.colorScheme.surface)
                                    ) {
                                        unidadesDisponibles.forEach { unidad ->
                                            DropdownMenuItem(
                                                text = { Text(unidad) },
                                                onClick = {
                                                    productUnidadMedida = unidad
                                                    expandedUnidad = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Campo Precio
                            FormField(
                                label = stringResource(R.string.precio),
                                isRequired = true,
                                isValid = validacionesIndividuales["Precio"] ?: false
                            ) {
                                TextInputModificado(
                                    valor = productPrecio,
                                    label = stringResource(R.string.precio),
                                    placeholder = stringResource(R.string.ingresarPrecio),
                                    teclado = KeyboardType.Decimal,
                                    onValueChange = { productPrecio = it },
                                    isError = productPrecio.isNotEmpty() && productPrecio.toFloatOrNull() == null,
                                    prefix = "$"
                                )
                            }

                            // Campo Presentación (Opcional)
                            FormField(
                                label = "Presentación (Opcional)",
                                isRequired = false,
                                isValid = true
                            ) {
                                TextInputModificado(
                                    valor = productPresentacion,
                                    label = "Presentación",
                                    placeholder = "Ej: 1kg de carne molida, 6 unidades",
                                    teclado = KeyboardType.Text,
                                    onValueChange = { productPresentacion = it },
                                    isError = false
                                )
                            }

                            // Vista previa de presentación
                            if (productCantidad.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            colorResource(R.color.principal).copy(alpha = 0.05f),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = colorResource(R.color.principal).copy(alpha = 0.05f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Vista previa:",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = productPresentacion.ifEmpty { "$productCantidad ${obtenerUnidadLimpia(productUnidadMedida)}" },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = colorResource(R.color.principal)
                                        )
                                    }
                                }
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
                        fieldsCompleted = validacionesIndividuales.values.count { it },
                        totalFields = 4
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    isRequired: Boolean = false,
    isValid: Boolean = true,
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
            if (isRequired && isValid) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Green
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
        placeholder = {
            Text(
                placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        },
        prefix = prefix?.let { { Text(it, style = MaterialTheme.typography.bodyLarge) } },
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = Modifier
            .fillMaxWidth()
            .focusable(true),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else colorResource(R.color.principal),
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else colorResource(
                R.color.gris_claro
            ),
            cursorColor = colorResource(R.color.principal),
            focusedLabelColor = if (isError) MaterialTheme.colorScheme.error else colorResource(R.color.principal),
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedContainerColor = colorResource(R.color.principal).copy(alpha = 0.02f),
            unfocusedContainerColor = Color.Transparent
        ),
        isError = isError,
        supportingText = if (isError && teclado == KeyboardType.Decimal) {
            { Text("Ingresa un valor válido", color = MaterialTheme.colorScheme.error) }
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