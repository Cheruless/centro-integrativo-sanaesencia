package cl.sanaesencia.app.ui.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.data.ServiceLocator
import cl.sanaesencia.app.domain.model.Paciente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen() {
    val coroutineScope = rememberCoroutineScope()
    var pacienteId by remember { mutableStateOf(0) }
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val paciente = ServiceLocator.pacienteRepository.getPacienteActual()
            pacienteId = paciente.id
            nombre = paciente.nombreCorto
            rut = paciente.rut
            correo = paciente.email
            telefono = paciente.telefono
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil del Paciente") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                            fechaNacimiento = null
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
