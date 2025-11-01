package com.mogars.buybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import com.mogars.buybuddy.ui.theme.BuyBuddyTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Hammer
import compose.icons.fontawesomeicons.solid.Home
import compose.icons.fontawesomeicons.solid.Search


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuyBuddyTheme {
                MainAdaptiveScaffold()
            }
        }
    }
}

@ExperimentalMaterial3ExpressiveApi
@Composable
fun MainAdaptiveScaffold() {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val isTablet = screenWidthDp >= 600
    var selectedItem by remember { mutableIntStateOf(0) }


    if (isTablet) {
        NavigationRailScaffold(selectedItem, onSelect = { selectedItem = it })
    } else {
        BottomBarScaffold(selectedItem, onSelect = { selectedItem = it })
    }
}

@Composable
fun BottomBarScaffold(selected: Int, onSelect: (Int) -> Unit) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected == 0,
                    onClick = { onSelect(0) },
                    //label = { Text("Home") },
                    icon = {
                        Icon(
                            FontAwesomeIcons.Solid.Home, null, modifier = Modifier.size(23.dp)
                        )
                    })
                NavigationBarItem(
                    selected == 1,
                    onClick = { onSelect(1) },
                    //label = { Text("Buscar") },
                    icon = {
                        Icon(
                            FontAwesomeIcons.Solid.Search, null, modifier = Modifier.size(23.dp)
                        )
                    })
                NavigationBarItem(
                    selected == 2,
                    onClick = { onSelect(2) },
                    //label = { Text(stringResource(R.string.configuracion)) },
                    icon = {
                        Icon(
                            FontAwesomeIcons.Solid.Hammer, null, modifier = Modifier.size(23.dp)
                        )
                    }
                )
            }
        }) { inner -> ScreenContent(Modifier.padding(inner), selected) }
}

@ExperimentalMaterial3ExpressiveApi
@Composable
fun NavigationRailScaffold(selected: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxSize()) {
        NavigationRail {
            NavigationRailItem(
                selected == 0,
                onClick = { onSelect(0) },
                //label = { Text("Home") },
                icon = {
                    Icon(
                        FontAwesomeIcons.Solid.Home,
                        null,
                        modifier = Modifier.size(23.dp)
                    )
                })
            NavigationRailItem(
                selected == 1,
                onClick = { onSelect(1) },
                //label = { Text("Buscar") },
                icon = {
                    Icon(
                        FontAwesomeIcons.Solid.Search,
                        null,
                        modifier = Modifier.size(23.dp)
                    )
                })
            NavigationRailItem(
                selected == 2,
                onClick = { onSelect(2) },
                //label = { Text(stringResource(R.string.configuracion)) },
                icon = {
                    Icon(
                        FontAwesomeIcons.Solid.Hammer,
                        null,
                        modifier = Modifier.size(23.dp)
                    )
                }
            )
        }
        ScreenContent(
            Modifier
                .fillMaxSize()
                .padding(16.dp), selected
        )
    }
}

@Composable
fun ScreenContent(modifier: Modifier, selected: Int) {
    Box(modifier) {
        when (selected) {
            0 -> HomeScreen()
            1 -> BuscarScreen()
            2 -> ConfigScreen()
        }
    }
}