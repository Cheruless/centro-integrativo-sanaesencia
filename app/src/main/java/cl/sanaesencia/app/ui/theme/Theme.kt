package cl.sanaesencia.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta natural del centro: verde salvia y tonos tierra.
private val EsquemaClaro = lightColorScheme(
    primary = Color(0xFF2F6B4F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB9EFD0),
    onPrimaryContainer = Color(0xFF002114),
    secondary = Color(0xFF7A5C3E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDB8),
    onSecondaryContainer = Color(0xFF2B1700),
    tertiary = Color(0xFF3F6375),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC3E8FD),
    onTertiaryContainer = Color(0xFF001F2B),
    background = Color(0xFFFBFDF8),
    onBackground = Color(0xFF191C1A),
    surface = Color(0xFFFBFDF8),
    onSurface = Color(0xFF191C1A),
    surfaceVariant = Color(0xFFDCE5DD),
    onSurfaceVariant = Color(0xFF404943),
    outline = Color(0xFF707973),
    outlineVariant = Color(0xFFC0C9C1),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF2F6F0),
    surfaceContainer = Color(0xFFECF1EA),
    surfaceContainerHigh = Color(0xFFE6EBE4),
    surfaceContainerHighest = Color(0xFFE1E6DF)
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFF9ED3B4),
    onPrimary = Color(0xFF003824),
    primaryContainer = Color(0xFF0F5137),
    onPrimaryContainer = Color(0xFFB9EFD0),
    secondary = Color(0xFFECBF91),
    onSecondary = Color(0xFF452B0F),
    secondaryContainer = Color(0xFF5E4125),
    onSecondaryContainer = Color(0xFFFFDDB8),
    tertiary = Color(0xFFA7CCE0),
    onTertiary = Color(0xFF0B3446),
    tertiaryContainer = Color(0xFF254B5D),
    onTertiaryContainer = Color(0xFFC3E8FD),
    background = Color(0xFF191C1A),
    onBackground = Color(0xFFE1E3DE),
    surface = Color(0xFF191C1A),
    onSurface = Color(0xFFE1E3DE),
    surfaceVariant = Color(0xFF404943),
    onSurfaceVariant = Color(0xFFC0C9C1),
    outline = Color(0xFF8A938C),
    outlineVariant = Color(0xFF404943),
    surfaceContainerLowest = Color(0xFF141714),
    surfaceContainerLow = Color(0xFF1F2320),
    surfaceContainer = Color(0xFF232724),
    surfaceContainerHigh = Color(0xFF2D312E),
    surfaceContainerHighest = Color(0xFF383C39)
)

@Composable
fun SanaesenciaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    @Suppress("UNUSED_PARAMETER") dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        content = content
    )
}
