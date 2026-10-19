package cl.sanaesencia.app.ui.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

/**
 * Solo EJECUTA la navegación si la pantalla
 * actual se encuentra en estado RESUMED (disponible o completado).
 * @author Exequiel
 */
fun NavHostController.navigateSafe(
    route: String,
    builder: (NavOptionsBuilder.() -> Unit)? = null
) {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        if (builder != null) {
            navigate(route, builder)
        } else {
            navigate(route)
        }
    }
}
