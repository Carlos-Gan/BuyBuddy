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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Search

@Preview
@Composable
fun BuscarScreen(
    onResultClick: (String) -> Unit = {},
    onAddClick: (String) -> Unit = {}
) {
    val textFieldState = rememberTextFieldState()

    val allItems = remember {
        listOf(
            "Angular", "Halloween", "JavaScript", "Kotlin", "Minecraft",
            "Pizza", "Python", "Queso", "React", "Vue", "Arroz"
        ).sorted()
    }

    val query = textFieldState.text.toString()

    val filteredItems by remember(query) {
        mutableStateOf(
            if (query.isBlank()) allItems
            else allItems.filter { it.contains(query, ignoreCase = true) }
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

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (query.isBlank() || filteredItems.isNotEmpty()) {
                items(filteredItems) { result ->
                    ResultadoItem(
                        text = result,
                        onClick = { onResultClick(result) }
                    )
                }
            } else { // No hay coincidencias
                item {
                    ResultadoItemAdd(
                        text = "${stringResource(R.string.agregar)} \"$query\"",
                        onClick = { onAddClick(query) }
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
        onValueChange = { textFieldState.edit { replace(0, length, it) } },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp)),
        placeholder = { Text("${stringResource(R.string.busca)}...") },
        leadingIcon = {
            Icon(
                FontAwesomeIcons.Solid.Search,
                contentDescription = stringResource(R.string.busca),
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun ResultadoItem(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ResultadoItemAdd(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFE7F1FF))
            .padding(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF007AFF)
        )
    }
}
