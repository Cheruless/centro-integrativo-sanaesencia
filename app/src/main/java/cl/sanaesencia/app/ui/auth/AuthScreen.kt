package cl.sanaesencia.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.data.ServiceLocator
import cl.sanaesencia.app.domain.model.Paciente
import cl.sanaesencia.app.ui.components.RutVisualTransformation
import cl.sanaesencia.app.ui.components.TelefonoVisualTransformation
import cl.sanaesencia.app.ui.theme.TextFieldBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoginMode by remember { mutableStateOf(true) }
    var rutLogin by remember { mutableStateOf("") }
    var nombreRegister by remember { mutableStateOf("") }
    var rutRegister by remember { mutableStateOf("") }
    var correoRegister by remember { mutableStateOf("") }
    var telefonoRegister by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sanaesencia",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isLoginMode) "Inicia sesión con tu RUT" else "Crea tu cuenta de paciente",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TabRow(
                        selectedTabIndex = if (isLoginMode) 0 else 1,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color.Transparent,
                        divider = {}
                    ) {
                        Tab(
                            selected = isLoginMode,
                            onClick = {
                                isLoginMode = true
                                errorMensaje = null
                            },
                            modifier = Modifier.padding(vertical = 12.dp),
                            text = { Text("Iniciar Sesión", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = !isLoginMode,
                            onClick = {
                                isLoginMode = false
                                errorMensaje = null
                            },
                            modifier = Modifier.padding(vertical = 12.dp),
                            text = { Text("Registrarse", fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (errorMensaje != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = errorMensaje!!,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (isLoginMode) {
                        TextField(
                            value = rutLogin,
                            onValueChange = { rutLogin = it.filter { c -> c.isDigit() || c.uppercaseChar() == 'K' }.take(9) },
                            label = { Text("RUT") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            visualTransformation = RutVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = TextFieldBackground,
                                unfocusedContainerColor = TextFieldBackground,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    } else {
                        TextField(
                            value = nombreRegister,
                            onValueChange = { nombreRegister = it },
                            label = { Text("Nombre completo") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = TextFieldBackground,
                                unfocusedContainerColor = TextFieldBackground,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = rutRegister,
                            onValueChange = { rutRegister = it.filter { c -> c.isDigit() || c.uppercaseChar() == 'K' }.take(9) },
                            label = { Text("RUT") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            visualTransformation = RutVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = TextFieldBackground,
                                unfocusedContainerColor = TextFieldBackground,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = correoRegister,
                            onValueChange = { correoRegister = it },
                            label = { Text("Correo electrónico") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = TextFieldBackground,
                                unfocusedContainerColor = TextFieldBackground,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = telefonoRegister,
                            onValueChange = { telefonoRegister = it.filter { c -> c.isDigit() }.take(8) },
                            label = { Text("Teléfono") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            visualTransformation = TelefonoVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = TextFieldBackground,
                                unfocusedContainerColor = TextFieldBackground,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            errorMensaje = null
                            if (isLoginMode) {
                                if (rutLogin.isBlank()) {
                                    errorMensaje = "Por favor ingresa tu RUT"
                                    return@Button
                                }
                                isLoading = true
                                coroutineScope.launch(Dispatchers.IO) {
                                    val pacienteEncontrado = ServiceLocator.pacienteRepository.getPacienteByRut(rutLogin.trim())
                                    if (pacienteEncontrado != null) {
                                        ServiceLocator.pacienteRepository.setPacienteActual(pacienteEncontrado.id)
                                        withContext(Dispatchers.Main) {
                                            isLoading = false
                                            onLoginSuccess()
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            isLoading = false
                                            errorMensaje = "RUT no registrado. Debes registrarte primero."
                                        }
                                    }
                                }
                            } else {
                                if (nombreRegister.isBlank() || rutRegister.isBlank() || correoRegister.isBlank() || telefonoRegister.isBlank()) {
                                    errorMensaje = "Por favor completa todos los campos"
                                    return@Button
                                }
                                isLoading = true
                                coroutineScope.launch(Dispatchers.IO) {
                                    val partesNombre = nombreRegister.trim().split(" ")
                                    val primerNombre = partesNombre.getOrNull(0) ?: ""
                                    val apellidoPaterno = partesNombre.getOrNull(1) ?: ""
                                    val apellidoMaterno = partesNombre.getOrNull(2) ?: ""

                                    val paciente = Paciente(
                                        id = 0,
                                        rut = rutRegister.trim(),
                                        primerNombre = primerNombre,
                                        segundoNombre = null,
                                        apellidoPaterno = apellidoPaterno,
                                        apellidoMaterno = apellidoMaterno,
                                        email = correoRegister.trim(),
                                        telefono = telefonoRegister.trim(),
                                        fechaNacimiento = null
                                    )
                                    ServiceLocator.pacienteRepository.updatePaciente(paciente)
                                    val registrado = ServiceLocator.pacienteRepository.getPacienteByRut(rutRegister.trim())
                                    if (registrado != null) {
                                        ServiceLocator.pacienteRepository.setPacienteActual(registrado.id)
                                    }
                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                        onLoginSuccess()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = if (isLoginMode) "Ingresar" else "Registrarse",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
