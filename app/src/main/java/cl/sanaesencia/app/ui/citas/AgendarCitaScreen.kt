package cl.sanaesencia.app.ui.citas

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.notificaciones.RecordatorioCitas
import cl.sanaesencia.app.ui.common.Formatos
import cl.sanaesencia.app.ui.common.MensajeError
import cl.sanaesencia.app.ui.common.Validaciones
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AgendarCitaScreen(
    viewModel: CitaViewModel,
    onVolverClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val formState by viewModel.formState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val especialistas by viewModel.especialistas.collectAsState()
    val scrollState = rememberScrollState()

    val serviciosDisponibles = remember {
        listOf(
            "Consulta de Psicología" to 45000.0,
            "Terapia Ocupacional" to 40000.0,
            "Masaje Descontracturante" to 35000.0,
            "Sesión de Aromaterapia" to 30000.0,
            "Atención Quiropráctica" to 50000.0,
            "Evaluación del Neurodesarrollo" to 70000.0
        )
    }
    val horasSugeridas = remember { listOf("09:00", "10:30", "12:00", "15:00", "16:30", "18:00") }

    var expandedEspecialistas by remember { mutableStateOf(false) }
    var mostrarFecha by remember { mutableStateOf(false) }
    var mostrarHora by remember { mutableStateOf(false) }

    // Permiso para notificaciones (Android 13+)
    val permisoNotificaciones = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (!RecordatorioCitas.tienePermiso(context)) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Cita guardada: programa el recordatorio, limpia el formulario y vuelve
    LaunchedEffect(formState.isAgendadoExitoso) {
        if (formState.isAgendadoExitoso) {
            RecordatorioCitas.programar(
                context = context,
                fecha = formState.fecha?.toString() ?: "",
                hora = formState.hora,
                servicio = formState.servicio,
                especialista = formState.especialistaSeleccionado?.nombreCorto ?: "tu especialista"
            )
            viewModel.resetFormState()
            onVolverClick()
        }
    }

    val horasOcupadas = remember(uiState.citas, formState.fecha) {
        viewModel.horasOcupadas(formState.fecha ?: LocalDate.now())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar Cita") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            // 1. Servicio
            Column {
                Text("1. Selecciona el servicio o terapia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    serviciosDisponibles.forEach { (serv, precio) ->
                        val selected = formState.servicio == serv
                        OutlinedCard(
                            onClick = { viewModel.onServicioChange(serv, precio) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                if (selected) 2.dp else 1.dp,
                                when {
                                    selected -> MaterialTheme.colorScheme.primary
                                    formState.servicioError != null -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.outlineVariant
                                }
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        serv,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                Text(
                                    Formatos.precio(precio),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                MensajeError(formState.servicioError)
            }

            HorizontalDivider()

            // 2. Especialista
            Column {
                Text("2. Elige el especialista", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedEspecialistas,
                    onExpandedChange = { expandedEspecialistas = !expandedEspecialistas }
                ) {
                    OutlinedTextField(
                        value = formState.especialistaSeleccionado?.nombreCompleto ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Especialista") },
                        placeholder = { Text("Selecciona un especialista") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEspecialistas) },
                        isError = formState.especialistaError != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEspecialistas,
                        onDismissRequest = { expandedEspecialistas = false }
                    ) {
                        especialistas.forEach { esp ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(esp.nombreCompleto, fontWeight = FontWeight.Bold)
                                        Text(
                                            esp.especialidadesLabel,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.onEspecialistaSeleccionado(esp)
                                    expandedEspecialistas = false
                                }
                            )
                        }
                    }
                }
                MensajeError(formState.especialistaError)
            }

            HorizontalDivider()

            // 3. Fecha
            Column {
                Text("3. Selecciona la fecha", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "El centro atiende de lunes a sábado.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedCard(
                    onClick = { mostrarFecha = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        if (formState.fechaError != null) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.outline
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (formState.fecha == null) "Toca para elegir una fecha" else Formatos.fecha(formState.fecha.toString()),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (formState.fecha == null) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                MensajeError(formState.fechaError)
            }

            HorizontalDivider()

            // 4. Hora
            Column {
                Text("4. Selecciona el horario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Atención de 09:00 a 19:00. Las horas en gris están ocupadas o ya pasaron.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    horasSugeridas.forEach { hora ->
                        val bloqueada = hora in horasOcupadas ||
                            (formState.fecha != null && Validaciones.validarHora(formState.fecha.toString(), hora) != null)
                        FilterChip(
                            selected = formState.hora == hora,
                            onClick = { viewModel.onHoraChange(hora) },
                            enabled = !bloqueada,
                            label = { Text(hora) },
                            leadingIcon = if (formState.hora == hora) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                    AssistChip(
                        onClick = { mostrarHora = true },
                        label = { Text("Otra hora") },
                        leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
                if (formState.hora.isNotBlank() && formState.hora !in horasSugeridas) {
                    Text(
                        "Hora elegida: ${formState.hora}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                MensajeError(formState.horaError)
            }

            HorizontalDivider()

            // 5. Notas
            Column {
                Text("5. Notas adicionales (opcional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formState.notas,
                    onValueChange = { viewModel.onNotasChange(it) },
                    label = { Text("Comentarios o motivo de consulta") },
                    supportingText = { Text("${formState.notas.length}/200") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }

            AnimatedVisibility(
                visible = formState.formError != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = formState.formError ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = { viewModel.agendarCita() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !formState.isSubmitting,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (formState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Confirmar y agendar cita", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ---- Selector de fecha (Material 3) ----
    if (mostrarFecha) {
        val hoy = remember { LocalDate.now() }
        val inicial = formState.fecha
            ?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = inicial,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val d = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                    return !d.isBefore(hoy) && d.dayOfWeek != DayOfWeek.SUNDAY
                }

                override fun isSelectableYear(year: Int): Boolean = year >= hoy.year
            }
        )
        DatePickerDialog(
            onDismissRequest = { mostrarFecha = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { ms ->
                        val d = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate()
                        viewModel.onFechaChange(d.toString())
                    }
                    mostrarFecha = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFecha = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ---- Selector de hora (Material 3) ----
    if (mostrarHora) {
        val timePickerState = rememberTimePickerState(initialHour = 10, initialMinute = 0, is24Hour = true)
        AlertDialog(
            onDismissRequest = { mostrarHora = false },
            title = { Text("Elige la hora") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onHoraChange("%02d:%02d".format(timePickerState.hour, timePickerState.minute))
                    mostrarHora = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarHora = false }) { Text("Cancelar") }
            }
        )
    }
}
