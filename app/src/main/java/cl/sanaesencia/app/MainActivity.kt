package cl.sanaesencia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.sanaesencia.app.data.ServiceLocator
import cl.sanaesencia.app.ui.navigation.MainScreen
import cl.sanaesencia.app.ui.theme.SanaesenciaTheme

/**
 * Activity principal de Sanaesencia. Punto de entrada de la app:
 * configura el tema Material 3 y carga MainScreen con toda la navegación.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(this)
        enableEdgeToEdge()
        setContent {
            SanaesenciaTheme {
                MainScreen()
            }
        }
    }
}