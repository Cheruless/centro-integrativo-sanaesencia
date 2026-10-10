package cl.sanaesencia.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    object Citas : Screen("citas", "Mis Citas", Icons.Default.CalendarMonth)
    object Especialistas : Screen("especialistas", "Especialistas", Icons.Default.MedicalServices)
    object Productos : Screen("productos", "Productos", Icons.Default.ShoppingBag)
    object Perfil : Screen("perfil", "Mi Perfil", Icons.Default.Person)

    object AgendarCita : Screen("agendar_cita", "Agendar Cita")
    object DetalleEspecialista : Screen("especialista_detail/{id}", "Detalle Especialista") {
        fun createRoute(id: Int) = "especialista_detail/$id"
    }
}

val bottomNavItems = listOf(
    Screen.Citas,
    Screen.Especialistas,
    Screen.Productos,
    Screen.Perfil
)
