package cl.sanaesencia.app.ui.citas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.model.EstadoCita

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasScreen(
    viewModel: CitaViewModel,
    onAgendarCitaClick: () -> Unit = {},
    onConfirmarClick: (Cita) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var citaParaCancelar by remember { mutableStateOf<Cita?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensajeExito, uiState.mensajeError) {
        uiState.mensajeExito?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensajes()
        }
        uiState.mensajeError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensajes()
        }
    }

    val citasFiltradas = remember(uiState.citas, uiState.filtroEstado) {
        if (uiState.filtroEstado == null) {
            uiState.citas
        } else {
            uiState.citas.filter { it.estado == uiState.filtroEstado }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAgendarCitaClick,
                icon = { Icon(Icons.Default.Add, contentDescription = "Agendar") },
                text = { Text("Agendar Cita") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Mis Citas Médicas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gestiona tus sesiones y terapias en Sanaesencia",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.filtroEstado == null,
                    onClick = { viewModel.setFiltroEstado(null) },
                    label = { Text("Todas (${uiState.citas.size})") }
                )
                FilterChip(
                    selected = uiState.filtroEstado == EstadoCita.CONFIRMADA,
                    onClick = { viewModel.setFiltroEstado(EstadoCita.CONFIRMADA) },
                    label = { Text("Confirmadas") }
                )
                FilterChip(
                    selected = uiState.filtroEstado == EstadoCita.PENDIENTE,
                    onClick = { viewModel.setFiltroEstado(EstadoCita.PENDIENTE) },
                    label = { Text("Pendientes") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (citasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tienes citas agendadas",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onAgendarCitaClick) {
                            Text("Agendar primera cita")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(citasFiltradas, key = { it.id }) { cita ->
                        CitaCard(
                            cita = cita,
                            onCancelarClick = { citaParaCancelar = cita },
                            onConfirmarClick = { onConfirmarClick(cita) }
                        )
                    }
                }
            }
        }
    }

    citaParaCancelar?.let { cita ->
        AlertDialog(
            onDismissRequest = { citaParaCancelar = null },
            icon = { Icon(Icons.Default.Cancel, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("¿Cancelar cita?") },
            text = {
                Text("¿Estás seguro de que deseas cancelar tu cita para '${cita.servicio}' el día ${cita.fechaLabel} a las ${cita.horaLabel} hrs?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelarCita(cita.id)
                        citaParaCancelar = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sí, cancelar cita")
                }
            },
            dismissButton = {
                TextButton(onClick = { citaParaCancelar = null }) {
                    Text("Volver")
                }
            }
        )
    }
}

@Composable
fun CitaCard(
    cita: Cita,
    onCancelarClick: () -> Unit,
    onConfirmarClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${cita.fechaLabel} - ${cita.horaLabel} hrs",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                EstadoChip(estado = cita.estado)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = cita.servicio,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${cita.especialista.nombreCompleto} (${cita.especialidad.displayName})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            cita.notas?.let { notas ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notas: $notas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Precio: $${cita.precio.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (cita.estado == EstadoCita.PENDIENTE) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onConfirmarClick,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Confirmar")
                        }
                        OutlinedButton(
                            onClick = onCancelarClick,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancelar")
                        }
                    }
                } else if (cita.estado != EstadoCita.CANCELADA && cita.estado != EstadoCita.REALIZADA) {
                    OutlinedButton(
                        onClick = onCancelarClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

@Composable
fun EstadoChip(estado: EstadoCita) {
    val (bgColor, textColor, label, icon) = when (estado) {
        EstadoCita.CONFIRMADA -> Quadruple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            "Confirmada",
            Icons.Default.CheckCircle
        )
        EstadoCita.PENDIENTE -> Quadruple(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            "Pendiente",
            Icons.Default.Schedule
        )
        EstadoCita.REALIZADA -> Quadruple(
            Color(0xFFE3F2FD),
            Color(0xFF1565C0),
            "Realizada",
            Icons.Default.CheckCircle
        )
        EstadoCita.CANCELADA -> Quadruple(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            "Cancelada",
            Icons.Default.Cancel
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = textColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
