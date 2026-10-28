package cl.sanaesencia.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.sanaesencia.app.data.ServiceLocator
import cl.sanaesencia.app.domain.model.Paciente
import cl.sanaesencia.app.ui.auth.AuthScreen
import cl.sanaesencia.app.ui.carrito.CarritoScreen
import cl.sanaesencia.app.ui.citas.AgendarPaso1Screen
import cl.sanaesencia.app.ui.citas.AgendarPaso2Screen
import cl.sanaesencia.app.ui.citas.AgendarPaso3Screen
import cl.sanaesencia.app.ui.citas.CitaViewModel
import cl.sanaesencia.app.ui.citas.CitasScreen
import cl.sanaesencia.app.ui.citas.ProcesandoPagoScreen
import cl.sanaesencia.app.ui.especialistas.EspecialistaViewModel
import cl.sanaesencia.app.ui.especialistas.EspecialistasScreen
import cl.sanaesencia.app.ui.home.HomeScreen
import cl.sanaesencia.app.ui.perfil.PerfilScreen
import cl.sanaesencia.app.ui.productos.ProductosScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    citaViewModel: CitaViewModel = viewModel(),
    especialistaViewModel: EspecialistaViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var paciente by remember { mutableStateOf<Paciente?>(null) }
    LaunchedEffect(currentRoute) {
        withContext(Dispatchers.IO) {
            paciente = ServiceLocator.pacienteRepository.getPacienteActual()
        }
    }

    val currentBottomNavItems = when (currentRoute) {
        Screen.Productos.route, Screen.Carrito.route -> listOf(Screen.Home, Screen.Carrito)
        else -> listOf(Screen.Home)
    }

    val showBottomBar = drawerNavItems.any { it.route == currentRoute }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = paciente?.primerNombre?.firstOrNull()?.uppercase() ?: "S",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = paciente?.nombreCompleto ?: "Paciente Sanaesencia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = paciente?.email ?: "contacto@sanaesencia.cl",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                drawerNavItems.forEach { screen ->
                    NavigationDrawerItem(
                        icon = {
                            screen.icon?.let { icon ->
                                Icon(imageVector = icon, contentDescription = screen.title)
                            }
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            if (currentRoute != screen.route) {
                                navController.navigateSafe(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        currentBottomNavItems.forEach { screen ->
                            NavigationBarItem(
                                selected = currentRoute == screen.route,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigateSafe(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    screen.icon?.let { icon ->
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = screen.title
                                        )
                                    }
                                },
                                label = { Text(screen.title) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Auth.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { fadeIn(animationSpec = tween(700)) },
                exitTransition = { fadeOut(animationSpec = tween(700)) }
            ) {
                composable(Screen.Auth.route) {
                    AuthScreen(
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Auth.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                        onNavigateCitas = { navController.navigateSafe(Screen.Citas.route) },
                        onNavigateEspecialistas = { navController.navigateSafe(Screen.Especialistas.route) },
                        onNavigateProductos = { navController.navigateSafe(Screen.Productos.route) },
                        onNavigateAgendar = { navController.navigateSafe(Screen.AgendarPaso1.createRoute()) }
                    )
                }

                composable(Screen.Citas.route) {
                    CitasScreen(
                        viewModel = citaViewModel,
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                        onAgendarCitaClick = {
                            navController.navigateSafe(Screen.AgendarPaso1.createRoute())
                        },
                        onConfirmarClick = { cita ->
                            navController.navigateSafe(Screen.ProcesandoPago.createRoute(cita.id))
                        }
                    )
                }

                composable(
                    route = Screen.AgendarPaso1.route,
                    arguments = listOf(
                        navArgument("especialistaId") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { backStackEntry ->
                    val especialistaId = backStackEntry.arguments
                        ?.getInt("especialistaId")?.takeIf { it != -1 }
                    AgendarPaso1Screen(
                        viewModel = citaViewModel,
                        onVolverClick = { navController.popBackStack() },
                        onContinuarClick = {
                            navController.navigateSafe(Screen.AgendarPaso2.route)
                        },
                        especialistaPreseleccionadoId = especialistaId
                    )
                }

                composable(Screen.AgendarPaso2.route) {
                    AgendarPaso2Screen(
                        viewModel = citaViewModel,
                        onVolverClick = { navController.popBackStack() },
                        onContinuarClick = {
                            navController.navigateSafe(Screen.AgendarPaso3.route)
                        }
                    )
                }

                composable(Screen.AgendarPaso3.route) {
                    AgendarPaso3Screen(
                        viewModel = citaViewModel,
                        onCancelarClick = {
                            navController.navigateSafe(Screen.Citas.route) {
                                popUpTo(Screen.Citas.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        onConfirmarClick = {
                            navController.navigateSafe(Screen.Citas.route) {
                                popUpTo(Screen.Citas.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        onConfirmarYPagarClick = { citaId ->
                            navController.navigateSafe(Screen.ProcesandoPago.createRoute(citaId))
                        }
                    )
                }

                composable(
                    route = Screen.ProcesandoPago.route,
                    arguments = listOf(
                        navArgument("citaId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val citaId = backStackEntry.arguments?.getInt("citaId") ?: return@composable
                    ProcesandoPagoScreen(
                        viewModel = citaViewModel,
                        citaId = citaId,
                        onPagoCompletado = {
                            navController.navigateSafe(Screen.Citas.route) {
                                popUpTo(Screen.Citas.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Screen.Especialistas.route) {
                    EspecialistasScreen(
                        viewModel = especialistaViewModel,
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                        onAgendarCitaClick = { especialistaId ->
                            navController.navigateSafe(Screen.AgendarPaso1.createRoute(especialistaId))
                        }
                    )
                }
                composable(Screen.Productos.route) {
                    ProductosScreen(
                        onMenuClick = { coroutineScope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.Carrito.route) {
                    CarritoScreen(
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                        onProcederPagoClick = {
                            navController.navigateSafe(Screen.ProcesandoPago.createRoute(1))
                        }
                    )
                }
                composable(Screen.Perfil.route) {
                    PerfilScreen(
                        onMenuClick = { coroutineScope.launch { drawerState.open() } }
                    )
                }
            }
        }
    }
}
