package cl.sanaesencia.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    object Auth : Screen("auth", "Autenticación")
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Citas : Screen("citas", "Mis Citas", Icons.Default.CalendarMonth)
    object Especialistas : Screen("especialistas", "Especialistas", Icons.Default.MedicalServices)
    object Productos : Screen("productos", "Productos", Icons.Default.ShoppingBag)
    object Carrito : Screen("carrito", "Carro", Icons.Default.ShoppingCart)
    object Perfil : Screen("perfil", "Mi Perfil", Icons.Default.Person)

    object AgendarPaso1 : Screen("agendar_paso1?especialistaId={especialistaId}", "Seleccionar Especialidad") {
        fun createRoute(especialistaId: Int? = null): String =
            if (especialistaId != null) "agendar_paso1?especialistaId=$especialistaId"
            else "agendar_paso1"
    }

    object AgendarPaso2 : Screen("agendar_paso2", "Resultados")

    object AgendarPaso3 : Screen("agendar_paso3", "Confirmar Cita")

    object ProcesandoPago : Screen("procesando_pago/{citaId}", "Procesando pago") {
        fun createRoute(citaId: Int) = "procesando_pago/$citaId"
    }
}

val drawerNavItems = listOf(
    Screen.Home,
    Screen.Citas,
    Screen.Especialistas,
    Screen.Productos,
    Screen.Carrito,
    Screen.Perfil
)

val bottomNavItems = listOf(
    Screen.Home
)
