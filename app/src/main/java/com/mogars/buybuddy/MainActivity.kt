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
import com.mogars.buybuddy.ViewModels.HomeViewModel
import com.mogars.buybuddy.ViewModels.HomeViewModelFactory
import com.mogars.buybuddy.data.PreferencesManager
import com.mogars.buybuddy.data.local.AppDatabase
import com.mogars.buybuddy.data.repository.ProductoRepository
import com.mogars.buybuddy.ui.theme.BuyBuddyTheme
import com.mogars.buybuddy.viewModel.AgregarViewModel
import com.mogars.buybuddy.viewModel.AgregarViewModelFactory
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Hammer
import compose.icons.fontawesomeicons.solid.Home
import compose.icons.fontawesomeicons.solid.Search

class MainActivity : ComponentActivity() {
    //  Crear instancia de la base de datos
    private val database by lazy { AppDatabase.getInstance(this) }
    private val repository by lazy {
        ProductoRepository(
            productoDao = database.productoDao(),
            precioDao = database.precioDao()
        )
    }

    private val preferencesManager by lazy {
        PreferencesManager(this)
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuyBuddyTheme {
                MainAdaptiveScaffold(repository = repository, preferencesManager = preferencesManager)
            }
        }
    }
}

@ExperimentalMaterial3ExpressiveApi
@Composable
fun MainAdaptiveScaffold(repository: ProductoRepository, preferencesManager: PreferencesManager) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val isTablet = screenWidthDp >= 600
    val navController = rememberNavController()

    if (isTablet) {
        NavigationRailScaffold(navController = navController, repository = repository, preferencesManager = preferencesManager)
    } else {
        BottomBarScaffold(navController = navController, repository = repository, preferencesManager = preferencesManager)
    }
}

@Composable
fun BottomBarScaffold(
    navController: NavHostController,
    repository: ProductoRepository,
    preferencesManager: PreferencesManager
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // ✅ CLAVE: Crear el ViewModel UNA SOLA VEZ aquí
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository, preferencesManager)
    )

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
        // ✅ Pasar el viewModel ya creado
        ScreenContent(
            modifier = Modifier.padding(inner),
            navController = navController,
            repository = repository,
            preferencesManager = preferencesManager,
            homeViewModel = homeViewModel
        )
    }
}

@ExperimentalMaterial3ExpressiveApi
@Composable
fun NavigationRailScaffold(
    navController: NavHostController,
    repository: ProductoRepository,
    preferencesManager: PreferencesManager
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // ✅ CLAVE: Crear el ViewModel UNA SOLA VEZ aquí
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository, preferencesManager)
    )

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
        // ✅ Pasar el viewModel ya creado
        ScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            navController = navController,
            repository = repository,
            preferencesManager = preferencesManager,
            homeViewModel = homeViewModel
        )
    }
}

@Composable
fun ScreenContent(
    modifier: Modifier,
    navController: NavHostController,
    repository: ProductoRepository,
    preferencesManager: PreferencesManager,
    homeViewModel: HomeViewModel  // ✅ NUEVO: Recibir el ViewModel como parámetro
) {
    Box(modifier) {
        NavHost(navController = navController, startDestination = "home") {
            // Home Screen
            composable("home") {
                // ✅ Usar el ViewModel pasado en lugar de crear uno nuevo
                HomeScreen(viewModel = homeViewModel)
            }
            // Buscar Screen
            composable("buscar") {
                BuscarScreen(navController, repository)
            }
            composable("config") { ConfigScreen(preferencesManager = preferencesManager) }
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