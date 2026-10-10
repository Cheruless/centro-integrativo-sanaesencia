package cl.sanaesencia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.sanaesencia.app.ui.navigation.MainScreen
import cl.sanaesencia.app.ui.theme.SanaesenciaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SanaesenciaTheme {
                MainScreen()
            }
        }
    }
}