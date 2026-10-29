package cl.sanaesencia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import cl.sanaesencia.app.domain.model.Paciente

/**
 * Avatar reutilizable del paciente. Muestra la foto de perfil si existe,
 * o las iniciales del nombre como fallback. Se usa en Home, drawer y Perfil.
 * Patrón: Composable reutilizable + Single Source of Truth (lee de Paciente).
 */
@Composable
fun PacienteAvatar(
    paciente: Paciente?,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val fotoUri = paciente?.fotoUri

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        if (fotoUri != null) {
            AsyncImage(
                model = fotoUri.toUri(),
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            val iniciales = (paciente?.primerNombre?.firstOrNull() ?: 'S').uppercaseChar()
            Text(
                text = iniciales.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
