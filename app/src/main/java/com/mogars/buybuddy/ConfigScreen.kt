package com.mogars.buybuddy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mogars.buybuddy.data.PreferencesManager
import kotlinx.coroutines.launch

@Composable
fun ConfigScreen(preferencesManager: PreferencesManager) {
    // Estado para guardar preferencias
    var ocultarCompletados by rememberSaveable { mutableStateOf(true) }
    var mostrarPrecioPromedio by rememberSaveable { mutableStateOf(false) }
    var ordenarPorNombre by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Cargar preferencias
        preferencesManager.obtenerOcultarCompletados().collect {
            ocultarCompletados = it
        }
        preferencesManager.obtenerMostrarPrecioPromedio().collect {
            mostrarPrecioPromedio = it
        }
        preferencesManager.obtenerOrdenarAlfabeticamente().collect {
            ordenarPorNombre = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 15.dp)
            .paddingFromBaseline(top = 35.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Text(
            text = stringResource(R.string.configuracion),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.principal)
        )

        // Sección: Productos Completados
        ConfigSection(
            title = stringResource(R.string.productosCompletados),
            description = stringResource(R.string.controlProductosComprados)
        ) {
            ConfigOptionItem(
                title = stringResource(R.string.ocultarProductosCompletados),
                subtitle = stringResource(R.string.productosMarcadosNoAparecen),
                isEnabled = ocultarCompletados,
                onToggle = { nuevoValor ->
                    ocultarCompletados = nuevoValor
                    coroutineScope.launch {
                        preferencesManager.guardarOcultarCompletados(nuevoValor)
                    }
                }
            )

            ConfigOptionItem(
                title = stringResource(R.string.marcar_autom_ticamente_como_inactivo),
                subtitle = stringResource(R.string.marcarCompletadoDesactivaAuto),
                isEnabled = true,
                onToggle = { }
            )
        }

        // Sección: Visualización
        ConfigSection(
            title = stringResource(R.string.visualizacion),
            description = stringResource(R.string.personalizacionListaCompras)
        ) {
            ConfigOptionItem(
                title = stringResource(R.string.mostrar_precio_promedio),
                subtitle = stringResource(R.string.muestra_el_precio_promedio_hist_rico_de_cada_producto),
                isEnabled = mostrarPrecioPromedio,
                onToggle = { nuevoValor ->
                    mostrarPrecioPromedio = nuevoValor
                    coroutineScope.launch {
                        preferencesManager.guardarMostrarPrecioPromedio(nuevoValor)
                    }
                }
            )

            ConfigOptionItem(
                title = stringResource(R.string.ordenar_alfab_ticamente),
                subtitle = stringResource(R.string.ordena_los_productos_por_nombre),
                isEnabled = ordenarPorNombre,
                onToggle = { nuevoValor ->
                    ordenarPorNombre = nuevoValor
                    coroutineScope.launch {
                        preferencesManager.guardarOrdenarAlfabeticamente(nuevoValor)
                    }
                }
            )
        }

        // Sección: Información
        ConfigSection(
            title = stringResource(R.string.infoConfig),
            description = stringResource(R.string.detallesAplicacion)
        ) {
            InfoItem(
                label = stringResource(R.string.version),
                value = stringResource(R.string.version_num)
            )
            InfoItem(
                label = stringResource(R.string.baseDatos),
                value = stringResource(R.string.bd_tipo)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ConfigSection(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.principal)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun ConfigOptionItem(
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            modifier = Modifier.padding(start = 12.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(R.color.principal)
            )
        )
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = colorResource(R.color.principal),
            fontWeight = FontWeight.Bold
        )
    }
}