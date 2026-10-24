package cl.sanaesencia.app.ui.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter

/**
 * Paso 3 del agendamiento: Confirmar Cita.
 * Muestra el resumen completo y 3 acciones:
 * Cancelar (volver al inicio), Confirmar (PENDIENTE) y Confirmar y pagar (pago mock).
 * @author Exequiel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarPaso3Screen(
    viewModel: CitaViewModel,
    onCancelarClick: () -> Unit = {},
    onConfirmarClick: () -> Unit = {},
    onConfirmarYPagarClick: (citaId: Int) -> Unit = {}
) {
    val formState by viewModel.formState.collectAsState()
    val scrollState = rememberScrollState()
    val fmt = DateTimeFormatter.ofPattern("HH:mm")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Cita") },
                navigationIcon = {
                    IconButton(onClick = onCancelarClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Revisa los detalles de tu cita",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // --- Resumen ---
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    val especialidad = formState.especialidad
                    val especialista = formState.especialistaSeleccionado
                    val fecha = formState.fecha
                    val hora = formState.hora

                    if (especialidad != null) {
                        Text(
                            text = especialidad.displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Duración: ${especialidad.duracionMinutos} minutos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    if (especialista != null) {
                        DetalleFila("Especialista", especialista.nombreCompleto)
                    }
                    if (fecha != null) {
                        DetalleFila("Fecha", fecha.toString())
                    }
                    if (hora != null && especialidad != null) {
                        val horaFin = hora.plusMinutes(especialidad.duracionMinutos.toLong())
                        DetalleFila("Hora", "${hora.format(fmt)} – ${horaFin.format(fmt)} hrs")
                    }
                    if (especialidad != null) {
                        DetalleFila(
                            "Precio",
                            "$${(formState.precio ?: especialidad.precioBase).toInt()}"
                        )
                    }

                    formState.notas.ifBlank { null }?.let { notas ->
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Text(
                            text = "Notas",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = notas,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // --- Notas ---
            OutlinedTextField(
                value = formState.notas,
                onValueChange = { viewModel.onNotasChange(it) },
                label = { Text("Comentarios o motivo de consulta (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            formState.formError?.let { err ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Botones de acción ---
            Text(
                text = "¿Qué deseas hacer?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            // Confirmar y pagar (primario)
            Button(
                onClick = {
                    val id = viewModel.agendarParaPago()
                    if (id != null) {
                        onConfirmarYPagarClick(id)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !formState.isSubmitting,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Confirmar y pagar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Confirmar (sin pago)
            OutlinedButton(
                onClick = {
                    val exito = viewModel.agendarCita()
                    if (exito != null) {
                        onConfirmarClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !formState.isSubmitting,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Confirmar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Cancelar
            TextButton(
                onClick = onCancelarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !formState.isSubmitting
            ) {
                Text(
                    text = "Cancelar",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetalleFila(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
