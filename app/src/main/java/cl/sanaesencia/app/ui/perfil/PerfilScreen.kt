package cl.sanaesencia.app.ui.perfil

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onMenuClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var pacienteId by remember { mutableStateOf(0) }
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
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
        uri?.let {
            fotoUriStr = it.toString()
            bitmapPreview = null
            coroutineScope.launch(Dispatchers.IO) {
                val pacienteActual = ServiceLocator.pacienteRepository.getPacienteActual()
                val partesNombre = nombre.trim().split(" ")
                val primerNombre = partesNombre.getOrNull(0) ?: ""
                val apellidoPaterno = partesNombre.getOrNull(1) ?: ""
                val apellidoMaterno = partesNombre.getOrNull(2) ?: ""

                val actualizado = Paciente(
                    id = pacienteId,
                    rut = rut,
                    primerNombre = primerNombre,
                    segundoNombre = pacienteActual.segundoNombre,
                    apellidoPaterno = apellidoPaterno,
                    apellidoMaterno = apellidoMaterno,
                    email = correo,
                    telefono = telefono,
                    fechaNacimiento = pacienteActual.fechaNacimiento,
                    fotoUri = fotoUriStr
                )
                ServiceLocator.pacienteRepository.updatePaciente(actualizado)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            bitmapPreview = it
            fotoUriStr = null
            coroutineScope.launch(Dispatchers.IO) {
                val pacienteActual = ServiceLocator.pacienteRepository.getPacienteActual()
                val partesNombre = nombre.trim().split(" ")
                val primerNombre = partesNombre.getOrNull(0) ?: ""
                val apellidoPaterno = partesNombre.getOrNull(1) ?: ""
                val apellidoMaterno = partesNombre.getOrNull(2) ?: ""

                val actualizado = Paciente(
                    id = pacienteId,
                    rut = rut,
                    primerNombre = primerNombre,
                    segundoNombre = pacienteActual.segundoNombre,
                    apellidoPaterno = apellidoPaterno,
                    apellidoMaterno = apellidoMaterno,
                    email = correo,
                    telefono = telefono,
                    fechaNacimiento = pacienteActual.fechaNacimiento,
                    fotoUri = null
                )
                ServiceLocator.pacienteRepository.updatePaciente(actualizado)
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
            nombre = paciente.nombreCorto
            rut = paciente.rut
            correo = paciente.email
            telefono = paciente.telefono
            fotoUriStr = paciente.fotoUri
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
                value = rut,
                onValueChange = { rut = it },
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
                        val nombresPartes = nombre.trim().split(" ")
                        val primerNombre = nombresPartes.getOrNull(0) ?: ""
                        val apellidoPaterno = nombresPartes.getOrNull(1) ?: ""
                        val apellidoMaterno = nombresPartes.getOrNull(2) ?: ""

                        val pacienteActualizado = Paciente(
                            id = pacienteId,
                            rut = rut,
                            primerNombre = primerNombre,
                            segundoNombre = null,
                            apellidoPaterno = apellidoPaterno,
                            apellidoMaterno = apellidoMaterno,
                            email = correo,
                            telefono = telefono,
                            fechaNacimiento = null,
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
