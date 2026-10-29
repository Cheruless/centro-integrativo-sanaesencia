package cl.sanaesencia.app.ui.perfil

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.sanaesencia.app.data.ServiceLocator
import cl.sanaesencia.app.domain.model.Paciente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import androidx.core.net.toUri

private fun guardarBitmapEnAlmacenamientoInterno(context: Context, bitmap: Bitmap, pacienteId: Int): Uri? {
    return try {
        val directory = File(context.filesDir, "profile_images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "paciente_${pacienteId}_profile.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun guardarUriEnAlmacenamientoInterno(context: Context, sourceUri: Uri, pacienteId: Int): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
        val directory = File(context.filesDir, "profile_images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "paciente_${pacienteId}_profile.jpg")
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun cargarBitmapDesdeUri(context: Context, uriStr: String?): Bitmap? {
    if (uriStr.isNullOrBlank()) return null
    return try {
        val uri = uriStr.toUri()
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    } catch (e: Exception) {
        try {
            val path = uriStr.toUri().path
            if (path != null && File(path).exists()) {
                BitmapFactory.decodeFile(path)
            } else {
                null
            }
        } catch (e2: Exception) {
            null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onMenuClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var pacienteId by remember { mutableStateOf(0) }
    var nombre by remember { mutableStateOf("") }
    var run by remember { mutableStateOf("") }
    var runDv by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var fotoUriStr by remember { mutableStateOf<String?>(null) }
    var bitmapPreview by remember { mutableStateOf<Bitmap?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var isLocating by remember { mutableStateOf(false) }

    val fetchLocation = {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            isLocating = true
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val provider = when {
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                    else -> null
                }
                val location = provider?.let { locationManager.getLastKnownLocation(it) }
                if (location != null) {
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            val resolvedAddress = addresses?.firstOrNull()?.getAddressLine(0) ?: "Ubicación actual"
                            withContext(Dispatchers.Main) {
                                direccion = resolvedAddress
                                isLocating = false
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                isLocating = false
                            }
                        }
                    }
                } else {
                    isLocating = false
                }
            } catch (e: Exception) {
                isLocating = false
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchLocation()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { sourceUri ->
            coroutineScope.launch(Dispatchers.IO) {
                val savedUri = guardarUriEnAlmacenamientoInterno(context, sourceUri, pacienteId)
                if (savedUri != null) {
                    val bitmap = cargarBitmapDesdeUri(context, savedUri.toString())
                    val pacienteActual = ServiceLocator.pacienteRepository.getPacienteActual()
                    val partesNombre = nombre.trim().split("\\s+".toRegex())
                    val primerNombre = partesNombre.getOrNull(0) ?: ""
                    val segundoNombre = if (partesNombre.size > 3) partesNombre.getOrNull(1) else null
                    val apellidoPaterno = if (partesNombre.size > 3) partesNombre.getOrNull(2) ?: "" else partesNombre.getOrNull(1) ?: ""
                    val apellidoMaterno = if (partesNombre.size > 3) partesNombre.getOrNull(3) ?: "" else partesNombre.getOrNull(2) ?: ""

                    val actualizado = pacienteActual.copy(
                        id = pacienteId,
                        primerNombre = primerNombre,
                        segundoNombre = segundoNombre,
                        apellidoPaterno = apellidoPaterno,
                        apellidoMaterno = apellidoMaterno,
                        email = correo,
                        telefono = telefono,
                        direccion = direccion,
                        fotoUri = savedUri.toString()
                    )
                    ServiceLocator.pacienteRepository.updatePaciente(actualizado)

                    withContext(Dispatchers.Main) {
                        fotoUriStr = savedUri.toString()
                        bitmapPreview = bitmap
                    }
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { capturedBitmap ->
            coroutineScope.launch(Dispatchers.IO) {
                val savedUri = guardarBitmapEnAlmacenamientoInterno(context, capturedBitmap, pacienteId)
                val uriString = savedUri?.toString()
                val pacienteActual = ServiceLocator.pacienteRepository.getPacienteActual()
                val partesNombre = nombre.trim().split("\\s+".toRegex())
                val primerNombre = partesNombre.getOrNull(0) ?: ""
                val segundoNombre = if (partesNombre.size > 3) partesNombre.getOrNull(1) else null
                val apellidoPaterno = if (partesNombre.size > 3) partesNombre.getOrNull(2) ?: "" else partesNombre.getOrNull(1) ?: ""
                val apellidoMaterno = if (partesNombre.size > 3) partesNombre.getOrNull(3) ?: "" else partesNombre.getOrNull(2) ?: ""

                val actualizado = pacienteActual.copy(
                    id = pacienteId,
                    primerNombre = primerNombre,
                    segundoNombre = segundoNombre,
                    apellidoPaterno = apellidoPaterno,
                    apellidoMaterno = apellidoMaterno,
                    email = correo,
                    telefono = telefono,
                    direccion = direccion,
                    fotoUri = uriString
                )
                ServiceLocator.pacienteRepository.updatePaciente(actualizado)

                withContext(Dispatchers.Main) {
                    fotoUriStr = uriString
                    bitmapPreview = capturedBitmap
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val paciente = ServiceLocator.pacienteRepository.getPacienteActual()
            pacienteId = paciente.id
            nombre = paciente.nombreCompleto
            run = paciente.run
            runDv = paciente.runDv
            correo = paciente.email
            direccion = paciente.direccion ?: ""
            telefono = paciente.telefono
            fotoUriStr = paciente.fotoUri

            val bitmap = cargarBitmapDesdeUri(context, paciente.fotoUri)
            withContext(Dispatchers.Main) {
                bitmapPreview = bitmap
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil del Paciente") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                when {
                    bitmapPreview != null -> {
                        Image(
                            bitmap = bitmapPreview!!.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                cameraLauncher.launch(null)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Tomar foto",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            TextButton(
                onClick = { galleryLauncher.launch("image/*") }
            ) {
                Text("Seleccionar desde la galería")
            }

            if (mensajeExito != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = mensajeExito!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = Paciente.formatearRut(run, runDv),
                onValueChange = {},
                readOnly = true,
                label = { Text("RUT") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            if (hasFine || hasCoarse) {
                                fetchLocation()
                            } else {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }
                    ) {
                        if (isLocating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Obtener ubicación GPS",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val pacienteActual = ServiceLocator.pacienteRepository.getPacienteActual()
                        val partesNombre = nombre.trim().split("\\s+".toRegex())
                        val primerNombre = partesNombre.getOrNull(0) ?: ""
                        val segundoNombre = if (partesNombre.size > 3) partesNombre.getOrNull(1) else null
                        val apellidoPaterno = if (partesNombre.size > 3) partesNombre.getOrNull(2) ?: "" else partesNombre.getOrNull(1) ?: ""
                        val apellidoMaterno = if (partesNombre.size > 3) partesNombre.getOrNull(3) ?: "" else partesNombre.getOrNull(2) ?: ""

                        val pacienteActualizado = pacienteActual.copy(
                            id = pacienteId,
                            primerNombre = primerNombre,
                            segundoNombre = segundoNombre,
                            apellidoPaterno = apellidoPaterno,
                            apellidoMaterno = apellidoMaterno,
                            email = correo,
                            telefono = telefono,
                            direccion = direccion,
                            fotoUri = fotoUriStr
                        )
                        ServiceLocator.pacienteRepository.updatePaciente(pacienteActualizado)
                        withContext(Dispatchers.Main) {
                            mensajeExito = "Cambios guardados exitosamente"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cambios")
            }
        }
    }
}
