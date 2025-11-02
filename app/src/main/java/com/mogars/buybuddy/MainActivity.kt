package com.mogars.buybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mogars.buybuddy.ViewModels.AgregarViewModel
import com.mogars.buybuddy.ViewModels.AgregarViewModelFactory
import com.mogars.buybuddy.ViewModels.HomeViewModel
import com.mogars.buybuddy.ViewModels.HomeViewModelFactory
import com.mogars.buybuddy.data.local.AppDatabase
import com.mogars.buybuddy.data.repository.ProductoRepository
import com.mogars.buybuddy.ui.theme.BuyBuddyTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Hammer
import compose.icons.fontawesomeicons.solid.Home
import compose.icons.fontawesomeicons.solid.Search

class MainActivity : ComponentActivity() {
    //  Crear instania de la base de datos
    private val database by lazy { AppDatabase.getInstance(this) }
    private val repository by lazy {
        ProductoRepository(
            productoDao = database.productoDao(),
            precioDao = database.precioDao()
        )
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuyBuddyTheme {
                MainAdaptiveScaffold(repository = repository)
            }
        }
    }
}

@ExperimentalMaterial3ExpressiveApi
@Composable
fun MainAdaptiveScaffold(repository: ProductoRepository) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val isTablet = screenWidthDp >= 600
    val navController = rememberNavController()

    if (isTablet) {
        NavigationRailScaffold(navController = navController, repository = repository)
    } else {
        BottomBarScaffold(navController = navController, repository = repository)
    }
}

@Composable
fun BottomBarScaffold(navController: NavHostController, repository: ProductoRepository) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") { launchSingleTop = true } },
                    icon = {
                        Icon(
                            FontAwesomeIcons.Solid.Home,
                            null,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                )
                NavigationBarItem(
                    selected = currentRoute == "buscar",
                    onClick = { navController.navigate("buscar") { launchSingleTop = true } },
                    icon = {
                        Icon(
                            FontAwesomeIcons.Solid.Search,
                            null,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                )
                NavigationBarItem(
                    selected = currentRoute == "config",
                    onClick = { navController.navigate("config") { launchSingleTop = true } },
                    icon = {
                        Icon(
                            FontAwesomeIcons.Solid.Hammer,
                            null,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                )
            }
        }
    ) { inner ->
        ScreenContent(
            modifier = Modifier.padding(inner),
            navController = navController,
            repository = repository
        )
    }
}

@ExperimentalMaterial3ExpressiveApi
@Composable
fun NavigationRailScaffold(navController: NavHostController, repository: ProductoRepository) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(Modifier.fillMaxSize()) {
        NavigationRail {
            NavigationRailItem(
                selected = currentRoute == "home",
                onClick = { navController.navigate("home") { launchSingleTop = true } },
                icon = { Icon(FontAwesomeIcons.Solid.Home, null, modifier = Modifier.size(23.dp)) }
            )
            NavigationRailItem(
                selected = currentRoute == "buscar",
                onClick = { navController.navigate("buscar") { launchSingleTop = true } },
                icon = {
                    Icon(
                        FontAwesomeIcons.Solid.Search,
                        null,
                        modifier = Modifier.size(23.dp)
                    )
                }
            )
            NavigationRailItem(
                selected = currentRoute == "config",
                onClick = { navController.navigate("config") { launchSingleTop = true } },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            navController = navController,
            repository = repository
        )
    }
}

@Composable
fun ScreenContent(
    modifier: Modifier,
    navController: NavHostController,
    repository: ProductoRepository
) {
    Box(modifier) {
        NavHost(navController = navController, startDestination = "home") {
            // Home Screen
            composable("home") {
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(repository)
                )
                HomeScreen(viewModel = viewModel)
            }
            // Buscar Screen
            composable("buscar") {
                BuscarScreen(navController, repository)
            }
            composable("config") { ConfigScreen() }
            // Agregar Screen
            composable("AgregarScreen?nombre={nombre}") { backStackEntry ->
                val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
                val viewModel: AgregarViewModel = viewModel(
                    factory = AgregarViewModelFactory(repository)
                )
                AgregarScreen(
                    nombre = nombre,
                    onBackClick = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
        }
    }
}
