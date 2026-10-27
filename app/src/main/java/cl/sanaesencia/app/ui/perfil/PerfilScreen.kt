package cl.sanaesencia.app.ui.perfil

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.domain.model.EstadoCita
import cl.sanaesencia.app.notificaciones.RecordatorioCitas
import cl.sanaesencia.app.ui.citas.CitaViewModel
import cl.sanaesencia.app.ui.common.AvatarIniciales
import cl.sanaesencia.app.ui.common.Contacto
import cl.sanaesencia.app.ui.common.Formatos
import cl.sanaesencia.app.ui.common.Validaciones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private fun archivoFoto(context: Context) = File(context.filesDir, "foto_perfil.jpg")

private fun guardarFoto(context: Context, uri: Uri) {
    try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            archivoFoto(context).outputStream().use { output -> input.copyTo(output) }
        }
    } catch (e: Exception) {
        // Si falla, simplemente se mantiene la foto anterior
    }
}

private fun cargarFoto(context: Context): ImageBitmap? {
    val f = archivoFoto(context)
    if (!f.exists()) return null
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(f.path, limites)
    var muestra = 1
    while (limites.outWidth / muestra > 1024 || limites.outHeight / muestra > 1024) muestra *= 2
    val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
    return BitmapFactory.decodeFile(f.path, opciones)?.asImageBitmap()
}

@Composable
fun PerfilScreen(
    viewModel: CitaViewModel,
    onAgendarClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val paciente by viewModel.paciente.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarEditar by remember { mutableStateOf(false) }
    var fotoVersion by remember { mutableIntStateOf(0) }

    val foto by produceState<ImageBitmap?>(initialValue = null, fotoVersion) {
        value = withContext(Dispatchers.IO) { cargarFoto(context) }
    }

    val selectorFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                withContext(Dispatchers.IO) { guardarFoto(context, uri) }
                fotoVersion++
            }
        }
    }

    val permisoNotificaciones = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            RecordatorioCitas.probarAhora(context)
            scope.launch { snackbarHostState.showSnackbar("Recibirás una notificación en unos segundos.") }
        }
    }

    LaunchedEffect(uiState.mensajeExito) {
        uiState.mensajeExito?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensajes()
        }
    }

    // ---- Seguimiento del tratamiento ----
    val hoy = remember { LocalDate.now() }
    val citas = uiState.citas
    val realizadas = citas.count { it.estado == EstadoCita.REALIZADA }
    val canceladas = citas.count { it.estado == EstadoCita.CANCELADA }
    val proximas = citas.count {
        (it.estado == EstadoCita.PENDIENTE || it.estado == EstadoCita.CONFIRMADA) &&
            !it.fecha.isBefore(hoy)
    }
    val ultimaSesion = citas
        .filter { it.estado == EstadoCita.REALIZADA }
        .map { it.fecha }
        .maxOrNull()
    val diasSinSesion = ultimaSesion?.let { ChronoUnit.DAYS.between(it, hoy) }

    val verde = Color(0xFF2E7D32)
    val naranjo = Color(0xFFE65100)
    val rojo = Color(0xFFC62828)
    val gris = Color(0xFF616161)
    val (estadoTexto, estadoColor) = when {
        proximas > 0 -> "Al día" to verde
        diasSinSesion == null -> "Sin sesiones aún" to gris
        diasSinSesion <= 14 -> "Al día" to verde
        diasSinSesion <= 30 -> "Sin sesión reciente" to naranjo
        else -> "Riesgo de abandono" to rojo
    }
    val enRiesgo = proximas == 0 && (diasSinSesion != null && diasSinSesion > 14)

    val agendadas = realizadas + proximas
    val progresoObjetivo = if (agendadas == 0) 0f else realizadas.toFloat() / agendadas
    val progreso by animateFloatAsState(
        targetValue = progresoObjetivo,
        animationSpec = tween(900),
        label = "progreso"
    )

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera con foto
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                        .clickable {
                            selectorFoto.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val imagen = foto
                    if (imagen != null) {
                        Image(
                            bitmap = imagen,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        val p = paciente
                        AvatarIniciales(
                            iniciales = if (p != null) {
                                "${p.primerNombre.firstOrNull() ?: ' '}${p.apellidoPaterno.firstOrNull() ?: ' '}".trim().uppercase()
                            } else "?",
                            size = 104.dp
                        )
                    }
                }
                TextButton(onClick = {
                    selectorFoto.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) { Text("Cambiar foto") }

                Text(
                    text = paciente?.nombreCompleto ?: "Cargando...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                paciente?.let {
                    Text(it.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(it.telefono, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(onClick = { mostrarEditar = true }, enabled = paciente != null) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Editar datos de contacto")
                }
            }

            // Seguimiento del tratamiento
            ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Seguimiento del tratamiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(shape = RoundedCornerShape(16.dp), color = estadoColor.copy(alpha = 0.12f)) {
                        Text(
                            estadoTexto,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = estadoColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Estadistica(realizadas.toString(), "Realizadas")
                        Estadistica(proximas.toString(), "Próximas")
                        Estadistica(canceladas.toString(), "Canceladas")
                    }

                    Column {
                        Text(
                            "Avance de sesiones agendadas",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progreso },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                        )
                    }

                    Text(
                        text = if (ultimaSesion != null) {
                            "Última sesión: ${Formatos.fecha(ultimaSesion.toString())} (hace ${diasSinSesion ?: 0} días)"
                        } else "Aún no tienes sesiones realizadas.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (enRiesgo) {
                        Text(
                            "Llevas varios días sin sesión. Retomar a tiempo ayuda a dar continuidad a tu tratamiento.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onAgendarClick, modifier = Modifier.fillMaxWidth()) {
                            Text("Agendar mi próxima sesión")
                        }
                    }
                }
            }

            // Sede
            ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nuestra sede", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(Contacto.SEDE_DIRECCION, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = { Contacto.abrirMapas(context) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cómo llegar")
                    }
                }
            }

            // Recordatorios
            ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recordatorios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "Al agendar una cita te avisamos 1 hora antes para que no la olvides.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = {
                            if (RecordatorioCitas.tienePermiso(context)) {
                                RecordatorioCitas.probarAhora(context)
                                scope.launch { snackbarHostState.showSnackbar("Recibirás una notificación en unos segundos.") }
                            } else {
                                permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Probar recordatorio (5 s)") }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (mostrarEditar) {
        paciente?.let { p ->
            EditarContactoDialog(
                emailInicial = p.email,
                telefonoInicial = p.telefono,
                onDismiss = { mostrarEditar = false },
                onGuardar = { email, telefono ->
                    viewModel.actualizarContacto(email, telefono)
                    mostrarEditar = false
                }
            )
        }
    }
}

@Composable
private fun Estadistica(valor: String, etiqueta: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EditarContactoDialog(
    emailInicial: String,
    telefonoInicial: String,
    onDismiss: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    var email by remember { mutableStateOf(emailInicial) }
    var telefono by remember { mutableStateOf(telefonoInicial) }
    var intentado by remember { mutableStateOf(false) }

    val emailError = if (intentado) Validaciones.validarEmail(email) else null
    val telefonoError = if (intentado) Validaciones.validarTelefono(telefono) else null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar datos de contacto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    isError = telefonoError != null,
                    supportingText = { telefonoError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                intentado = true
                if (Validaciones.validarEmail(email) == null && Validaciones.validarTelefono(telefono) == null) {
                    onGuardar(email.trim(), telefono.trim())
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
