package cl.sanaesencia.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.sanaesencia.app.SanaesenciaApp
import cl.sanaesencia.app.ui.citas.AgendarPaso1Screen
import cl.sanaesencia.app.ui.citas.AgendarPaso2Screen
import cl.sanaesencia.app.ui.citas.AgendarPaso3Screen
import cl.sanaesencia.app.ui.citas.CitaViewModel
import cl.sanaesencia.app.ui.citas.CitaViewModelFactory
import cl.sanaesencia.app.ui.citas.CitasScreen
import cl.sanaesencia.app.ui.citas.ProcesandoPagoScreen
import cl.sanaesencia.app.ui.especialistas.EspecialistasScreen
import cl.sanaesencia.app.ui.perfil.PerfilScreen
import cl.sanaesencia.app.ui.productos.ProductosScreen

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    citaViewModel: CitaViewModel = viewModel(
        factory = CitaViewModelFactory(
            (LocalContext.current.applicationContext as SanaesenciaApp).citaRepository
        )
    )
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    // Abre el flujo de agendamiento en el Paso 1
    val irAAgendar: () -> Unit = {
        citaViewModel.resetFormState()
        navController.navigateSafe(Screen.AgendarPaso1.createRoute())
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
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
                                    Icon(imageVector = icon, contentDescription = screen.title)
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
            startDestination = Screen.Citas.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 8 } },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 8 } },
            popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(300)) { it / 8 } }
        ) {
            composable(Screen.Citas.route) {
                CitasScreen(
                    viewModel = citaViewModel,
                    onAgendarCitaClick = irAAgendar
                )
            }
            composable(
                route = Screen.AgendarPaso1.route,
                arguments = listOf(navArgument("especialistaId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val espIdArg = backStackEntry.arguments?.getInt("especialistaId")?.takeIf { it != -1 }
                AgendarPaso1Screen(
                    viewModel = citaViewModel,
                    onVolverClick = { navController.popBackStack() },
                    onContinuarClick = { navController.navigateSafe(Screen.AgendarPaso2.route) },
                    especialistaPreseleccionadoId = espIdArg
                )
            }
            composable(Screen.AgendarPaso2.route) {
                AgendarPaso2Screen(
                    viewModel = citaViewModel,
                    onVolverClick = { navController.popBackStack() },
                    onContinuarClick = { navController.navigateSafe(Screen.AgendarPaso3.route) }
                )
            }
            composable(Screen.AgendarPaso3.route) {
                AgendarPaso3Screen(
                    viewModel = citaViewModel,
                    onCancelarClick = { navController.popBackStack() },
                    onConfirmarClick = {
                        navController.navigate(Screen.Citas.route) {
                            popUpTo(Screen.Citas.route) { inclusive = true }
                        }
                    },
                    onConfirmarYPagarClick = { citaId ->
                        navController.navigateSafe(Screen.ProcesandoPago.createRoute(citaId))
                    }
                )
            }
            composable(
                route = Screen.ProcesandoPago.route,
                arguments = listOf(navArgument("citaId") { type = NavType.IntType })
            ) { backStackEntry ->
                val citaId = backStackEntry.arguments?.getInt("citaId") ?: 0
                ProcesandoPagoScreen(
                    viewModel = citaViewModel,
                    citaId = citaId,
                    onPagoCompletado = {
                        navController.navigate(Screen.Citas.route) {
                            popUpTo(Screen.Citas.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Especialistas.route) {
                EspecialistasScreen(
                    viewModel = citaViewModel,
                    onAgendarConEspecialista = { esp ->
                        citaViewModel.resetFormState()
                        navController.navigateSafe(Screen.AgendarPaso1.createRoute(esp.id))
                    }
                )
            }
            composable(Screen.Productos.route) {
                ProductosScreen(viewModel = citaViewModel)
            }
            composable(Screen.Perfil.route) {
                PerfilScreen(
                    viewModel = citaViewModel,
                    onAgendarClick = irAAgendar
                )
            }
        }
    }
}
