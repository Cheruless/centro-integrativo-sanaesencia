package cl.sanaesencia.app.ui.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.ui.components.MonthCalendar

/**
 * Paso 2 del agendamiento: Resultados de Especialidad.
 * Lista de especialistas arriba, calendario debajo, horas del especialista
 * seleccionado al elegir día. Filtros bidireccionales especialista ↔ día.
 * @author Exequiel
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AgendarPaso2Screen(
    viewModel: CitaViewModel,
    onVolverClick: () -> Unit = {},
    onContinuarClick: () -> Unit = {}
) {
    val formState by viewModel.formState.collectAsState()
    val scrollState = rememberScrollState()

    val seleccionCompleta = formState.especialistaSeleccionado != null &&
        formState.fecha != null &&
        formState.hora != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { if (seleccionCompleta) onContinuarClick() },
                containerColor = if (seleccionCompleta)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (seleccionCompleta)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continuar")
            }
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
            // --- Header + Borrar selección ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Resultados de ${formState.especialidad?.displayName ?: ""}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Selecciona especialista, día y hora",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = { viewModel.onBorrarSeleccion() }) {
                    Text("Borrar selección")
                }
            }

            // --- Lista de especialistas ---
            Text(
                text = "Especialistas",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            if (formState.especialistasFiltrados.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Ningún especialista disponible para esta selección.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                formState.especialistasFiltrados.forEach { esp ->
                    EspecialistaSelectCard(
                        especialista = esp,
                        selected = formState.especialistaSeleccionado?.id == esp.id,
                        onClick = { viewModel.onEspecialistaSeleccionado(esp) }
                    )
                }
            }

            HorizontalDivider()

            // --- Calendario ---
            Text(
                text = "Selecciona el día",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            MonthCalendar(
                mes = formState.mesVisible,
                fechaSeleccionada = formState.fecha,
                diasConDisponibilidad = formState.diasConDisponibilidad,
                onMesChange = { viewModel.onMesChange(it) },
                onFechaSeleccionada = { viewModel.onFechaChange(it) }
            )

            HorizontalDivider()

            // --- Horas disponibles ---
            Text(
                text = "Horas disponibles",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            val esp = formState.especialistaSeleccionado
            val fecha = formState.fecha

            when {
                esp == null || fecha == null -> {
                    Text(
                        text = "Selecciona un especialista y un día para ver las horas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                formState.bloquesDisponibles.isEmpty() -> {
                    Text(
                        text = "Sin horas disponibles para esta selección.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    Text(
                        text = "${esp.nombreCorto} · $fecha",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        formState.bloquesDisponibles.forEach { horario ->
                            val selected = formState.hora == horario.inicio
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.onHoraChange(horario.inicio) },
                                label = { Text(horario.etiqueta) },
                                leadingIcon = if (selected) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun EspecialistaSelectCard(
    especialista: Especialista,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = especialista.nombreCompleto,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = especialista.especialidadesLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
