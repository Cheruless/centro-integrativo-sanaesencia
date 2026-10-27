package cl.sanaesencia.app.ui.especialistas

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.ui.citas.CitaViewModel
import cl.sanaesencia.app.ui.common.AvatarIniciales
import cl.sanaesencia.app.ui.common.Contacto
import cl.sanaesencia.app.ui.common.EncabezadoPantalla

/** La especialidad que más aporta al centro. */
private fun esPsicologia(especialidad: String) =
    especialidad.contains("psicolog", ignoreCase = true)

private fun iniciales(e: Especialista) =
    "${e.primerNombre.firstOrNull() ?: ' '}${e.apellidoPaterno.firstOrNull() ?: ' '}".trim().uppercase()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EspecialistasScreen(
    viewModel: CitaViewModel,
    onAgendarConEspecialista: (Especialista) -> Unit = {}
) {
    val context = LocalContext.current
    val especialistas by viewModel.especialistas.collectAsState()
    var filtro by remember { mutableStateOf<String?>(null) }
    var seleccionado by remember { mutableStateOf<Especialista?>(null) }

    val especialidades = remember(especialistas) {
        especialistas.flatMap { it.especialidades }.map { it.displayName }.distinct()
            .sortedWith(compareByDescending<String> { esPsicologia(it) }.thenBy { it })
    }
    val lista = remember(especialistas, filtro) {
        especialistas
            .filter { filtro == null || it.especialidades.any { esp -> esp.displayName == filtro } }
            .sortedByDescending { it.especialidades.any { esp -> esPsicologia(esp.displayName) } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        EncabezadoPantalla(
            icono = Icons.Default.Person,
            titulo = "Especialistas",
            subtitulo = "Equipo multidisciplinario de Sanaesencia"
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filtro == null,
                onClick = { filtro = null },
                label = { Text("Todos (${especialistas.size})") }
            )
            especialidades.forEach { esp ->
                FilterChip(
                    selected = filtro == esp,
                    onClick = { filtro = if (filtro == esp) null else esp },
                    label = { Text(esp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (lista.isEmpty()) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    "No hay especialistas para mostrar",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(lista, key = { it.id }) { esp ->
                    ElevatedCard(
                        onClick = { seleccionado = esp },
                        modifier = Modifier.fillMaxWidth().animateItem(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarIniciales(iniciales(esp), size = 52.dp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    esp.nombreCompleto,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    esp.especialidadesLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (esp.especialidades.any { esPsicologia(it.displayName) }) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                "Especialidad principal",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    seleccionado?.let { esp ->
        ModalBottomSheet(onDismissRequest = { seleccionado = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarIniciales(iniciales(esp), size = 64.dp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(esp.nombreCompleto, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(esp.especialidadesLabel, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }

                if (esp.especialidades.any { esPsicologia(it.displayName) }) {
                    Text(
                        "Psicología es la especialidad principal del centro.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = esp.descripcion.ifBlank { "Profesional del equipo multidisciplinario de Sanaesencia." },
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (esp.email.isNotBlank()) {
                        OutlinedButton(onClick = { Contacto.escribir(context, esp.email) }) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Escribir")
                        }
                    }
                    if (esp.telefono.isNotBlank()) {
                        OutlinedButton(onClick = { Contacto.llamar(context, esp.telefono) }) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Llamar")
                        }
                    }
                }

                Button(
                    onClick = {
                        seleccionado = null
                        onAgendarConEspecialista(esp)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agendar con este especialista", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
