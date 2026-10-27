package cl.sanaesencia.app.ui.citas

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.model.EstadoCita
import cl.sanaesencia.app.notificaciones.RecordatorioCitas
import cl.sanaesencia.app.ui.common.EncabezadoPantalla
import cl.sanaesencia.app.ui.common.Formatos
import cl.sanaesencia.app.ui.common.Politicas

@Composable
fun CitasScreen(
    viewModel: CitaViewModel,
    onAgendarCitaClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var citaParaCancelar by remember { mutableStateOf<Cita?>(null) }
    var citaSinCancelacion by remember { mutableStateOf<Cita?>(null) }

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
        if (uiState.filtroEstado == null) uiState.citas
        else uiState.citas.filter { it.estado == uiState.filtroEstado }
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

            EncabezadoPantalla(
                icono = Icons.Default.CalendarMonth,
                titulo = "Mis Citas",
                subtitulo = "Gestiona tus sesiones y terapias en Sanaesencia"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Puedes cancelar hasta ${Politicas.HORAS_MIN_CANCELACION} horas antes de tu cita, así otra persona puede usar ese horario.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
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
                FilterChip(
                    selected = uiState.filtroEstado == EstadoCita.REALIZADA,
                    onClick = { viewModel.setFiltroEstado(EstadoCita.REALIZADA) },
                    label = { Text("Realizadas") }
                )
                FilterChip(
                    selected = uiState.filtroEstado == EstadoCita.CANCELADA,
                    onClick = { viewModel.setFiltroEstado(EstadoCita.CANCELADA) },
                    label = { Text("Canceladas") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else if (citasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
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
                            text = "No hay citas para mostrar",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onAgendarCitaClick) { Text("Agendar una cita") }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(citasFiltradas, key = { it.id }) { cita ->
                        CitaCard(
                            cita = cita,
                            modifier = Modifier.animateItem(),
                            onCancelarClick = {
                                if (viewModel.puedeCancelar(cita)) citaParaCancelar = cita
                                else citaSinCancelacion = cita
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmación de cancelación (cita con más de 24 h de anticipación)
    citaParaCancelar?.let { cita ->
        AlertDialog(
            onDismissRequest = { citaParaCancelar = null },
            icon = { Icon(Icons.Default.Cancel, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("¿Cancelar cita?") },
            text = {
                Text("¿Seguro que deseas cancelar tu cita de '${cita.servicio}' el ${Formatos.fecha(cita.fecha.toString())} a las ${cita.hora} hrs?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        RecordatorioCitas.cancelar(context, cita.fecha.toString(), cita.hora.toString(), cita.servicio)
                        viewModel.cancelarCita(cita.id)
                        citaParaCancelar = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Sí, cancelar cita") }
            },
            dismissButton = {
                TextButton(onClick = { citaParaCancelar = null }) { Text("Volver") }
            }
        )
    }

    // Aviso de política: faltan menos de 24 h
    citaSinCancelacion?.let { cita ->
        AlertDialog(
            onDismissRequest = { citaSinCancelacion = null },
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("No se puede cancelar desde la app") },
            text = {
                Text(
                    "Tu cita de '${cita.servicio}' es en menos de ${Politicas.HORAS_MIN_CANCELACION} horas. " +
                        "Por política del centro, las cancelaciones se hacen con al menos ${Politicas.HORAS_MIN_CANCELACION} horas de anticipación " +
                        "para que tu especialista pueda reorganizar su agenda. Si no puedes asistir, avisa directamente al centro."
                )
            },
            confirmButton = {
                TextButton(onClick = { citaSinCancelacion = null }) { Text("Entendido") }
            }
        )
    }
}

@Composable
fun CitaCard(
    cita: Cita,
    onCancelarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
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
                        text = "${Formatos.fecha(cita.fecha.toString())} · ${cita.hora}",
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
                    text = "${cita.especialista.nombreCompleto} (${cita.especialista.especialidadesLabel})",
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
                    text = Formatos.precio(cita.precio),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (cita.estado == EstadoCita.PENDIENTE || cita.estado == EstadoCita.CONFIRMADA) {
                    OutlinedButton(
                        onClick = onCancelarClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Cancelar") }
                }
            }
        }
    }
}

@Composable
fun EstadoChip(estado: EstadoCita) {
    val (bgColor, textColor, label, icon) = when (estado) {
        EstadoCita.CONFIRMADA -> Cuarteto(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Confirmada", Icons.Default.CheckCircle)
        EstadoCita.PENDIENTE -> Cuarteto(Color(0xFFFFF3E0), Color(0xFFE65100), "Pendiente", Icons.Default.Schedule)
        EstadoCita.REALIZADA -> Cuarteto(Color(0xFFE3F2FD), Color(0xFF1565C0), "Realizada", Icons.Default.CheckCircle)
        EstadoCita.CANCELADA -> Cuarteto(Color(0xFFFFEBEE), Color(0xFFC62828), "Cancelada", Icons.Default.Cancel)
    }

    Surface(color = bgColor, shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = textColor)
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

private data class Cuarteto<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
