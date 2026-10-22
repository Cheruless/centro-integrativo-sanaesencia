package cl.sanaesencia.app.ui.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pantalla del Perfil del Paciente registrado en Sanaesencia.
 *
 * TODO (Integrante asignado al Módulo Perfil y Pacientes):
 *  1. Crear `PerfilViewModel.kt` en este paquete (`cl.sanaesencia.app.ui.perfil`)
 *     utilizando `MockPacienteRepository`.
 *  2. Definir la data class `PerfilUiState` con la información del paciente activo y
 *     estados de edición.
 *  3. Diseñar formulario interactivo con campos de datos personales:
 *     - Nombre completo, RUT (con validación/máscara).
 *     - Correo electrónico y Teléfono de contacto.
 *     - Fecha de nacimiento.
 *  4. Permitir actualizar los datos del paciente llamando a `pacienteRepository.updatePaciente(...)`.
 *  5. Agregar una sección de "Historial de Consultas" o "Ajustes de Cuenta".
 */
@Composable
fun PerfilScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Perfil del Paciente",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Consulta y actualiza tus datos personales y de contacto.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
