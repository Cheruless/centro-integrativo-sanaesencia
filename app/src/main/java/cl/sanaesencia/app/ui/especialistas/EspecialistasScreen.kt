package cl.sanaesencia.app.ui.especialistas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pantalla principal del Catálogo de Especialistas de Sanaesencia.
 *
 * TODO (Integrante asignado al Módulo Especialistas):
 *  1. Crear `EspecialistaViewModel.kt` en este paquete (`cl.sanaesencia.app.ui.especialistas`)
 *     utilizando `MockEspecialistaRepository`.
 *  2. Definir la data class `EspecialistasUiState` (con lista de especialistas, filtro por especialidad,
 *     estado de carga y mensajes).
 *  3. Diseñar tarjetas para cada profesional (`EspecialistaCard`) mostrando:
 *     - Nombre completo (`especialista.nombreCompleto`).
 *     - Especialidad e icono representativo.
 *     - Descripción breve o biografía.
 *     - Botón de acción: "Agendar Cita con este Profesional".
 *  4. Implementar barra de búsqueda o chips de filtro por especialidad (ej. Psicología, Quiropraxia, etc.).
 */
@Composable
fun EspecialistasScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Catálogo de Especialistas",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Conoce a nuestros profesionales en psicología, masoterapia, aromaterapia y más.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
