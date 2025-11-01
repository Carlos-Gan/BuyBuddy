package com.mogars.buybuddy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.Save

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AgregarScreen(
    nombre: String? = "",
    onBackClick: () -> Unit = {},
    onSave: (String) -> Unit = {}
) {
    var productName by rememberSaveable { mutableStateOf(nombre ?: "") }

    Scaffold(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp)
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.agregarProducto),
                        color = colorResource(R.color.white),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontStyle = FontStyle.Normal
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.ArrowLeft,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Volver",
                            tint = colorResource(R.color.white)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onSave(productName) }) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Save,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Guardar",
                            tint = colorResource(R.color.white)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.principal)
                )
            )
        }
    ) { innerPadding ->
        ScrollContent(innerPadding) {
            // Aquí irá el contenido del formulario
            Text("Agregar nuevo producto")
        }
    }
}

@Composable
fun ScrollContent(
    innerPadding: PaddingValues,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun AgregarScreenPreview() {
    AgregarScreen()
}
