package cl.sanaesencia.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.sanaesencia.app.ui.citas.AgendarCitaScreen
import cl.sanaesencia.app.ui.citas.CitasScreen
import cl.sanaesencia.app.ui.especialistas.EspecialistasScreen
import cl.sanaesencia.app.ui.perfil.PerfilScreen
import cl.sanaesencia.app.ui.productos.ProductosScreen

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
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
            startDestination = Screen.Citas.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Citas.route) {
                CitasScreen(
                    onAgendarCitaClick = {
                        navController.navigate(Screen.AgendarCita.route)
                    }
                )
            }
            composable(Screen.AgendarCita.route) {
                AgendarCitaScreen(
                    onVolverClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Especialistas.route) {
                EspecialistasScreen()
            }
            composable(Screen.Productos.route) {
                ProductosScreen()
            }
            composable(Screen.Perfil.route) {
                PerfilScreen()
            }
        }
    }
}
