package cl.sanaesencia.app.ui.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarCitaScreen(
    viewModel: CitaViewModel,
    onVolverClick: () -> Unit = {}
) {
    val formState by viewModel.formState.collectAsState()
    val especialistas = viewModel.especialistas
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

    val horasDisponibles = remember {
        listOf("09:00", "10:30", "12:00", "15:00", "16:30", "18:00")
    }

    val fechasSugeridas = remember {
        listOf("2026-10-18", "2026-10-19", "2026-10-20", "2026-10-22")
    }

    var expandedEspecialistas by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.resetFormState()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar Cita Médica") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "1. Selecciona el Servicio o Terapia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                serviciosDisponibles.forEach { (serv, precio) ->
                    val selected = formState.servicio == serv
                    OutlinedCard(
                        onClick = {
                            viewModel.onServicioChange(serv)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = serv,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            Text(
                                text = "$${precio.toInt()}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            Text(
                text = "2. Elige el Especialista",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ExposedDropdownMenuBox(
                expanded = expandedEspecialistas,
                onExpandedChange = { expandedEspecialistas = !expandedEspecialistas }
            ) {
                OutlinedTextField(
                    value = formState.especialistaSeleccionado?.nombreCompleto ?: "Selecciona un especialista...",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especialista") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEspecialistas) },
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
                                    Text(
                                        text = esp.nombreCompleto,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = esp.especialidad,
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

            HorizontalDivider()

            Text(
                text = "3. Selecciona la Fecha",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = formState.fecha,
                onValueChange = { viewModel.onFechaChange(it) },
                label = { Text("Fecha (YYYY-MM-DD)") },
                placeholder = { Text("ej: 2026-10-20") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fechasSugeridas.forEach { fecha ->
                    FilterChip(
                        selected = formState.fecha == fecha,
                        onClick = { viewModel.onFechaChange(fecha) },
                        label = { Text(fecha) }
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = "4. Selecciona el Horario",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                horasDisponibles.take(3).forEach { hora ->
                    FilterChip(
                        selected = formState.hora == hora,
                        onClick = { viewModel.onHoraChange(hora) },
                        label = { Text(hora) },
                        leadingIcon = if (formState.hora == hora) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                horasDisponibles.drop(3).forEach { hora ->
                    FilterChip(
                        selected = formState.hora == hora,
                        onClick = { viewModel.onHoraChange(hora) },
                        label = { Text(hora) },
                        leadingIcon = if (formState.hora == hora) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = "5. Notas Adicionales (Opcional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = formState.notas,
                onValueChange = { viewModel.onNotasChange(it) },
                label = { Text("Comentarios o motivo de consulta") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            formState.formError?.let { err ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
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

            Button(
                onClick = {
                    val exito = viewModel.agendarCita()
                    if (exito) {
                        onVolverClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !formState.isSubmitting,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (formState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Confirmar y Agendar Cita",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
