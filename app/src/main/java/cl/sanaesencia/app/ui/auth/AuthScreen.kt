package cl.sanaesencia.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.data.ServiceLocator
import cl.sanaesencia.app.domain.model.Paciente
import cl.sanaesencia.app.ui.components.RutVisualTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

/** Genera hash SHA-256 de la contraseña para almacenarla de forma segura. */
fun hashPassword(password: String): String =
    MessageDigest.getInstance("SHA-256")
        .digest(password.toByteArray())
        .joinToString("") { "%02x".format(it) }

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoginMode by remember { mutableStateOf(true) }

    // Login
    var rutLogin by remember { mutableStateOf("") }
    var dvLogin by remember { mutableStateOf("") }
    var passwordLogin by remember { mutableStateOf("") }
    var showPasswordLogin by remember { mutableStateOf(false) }

    // Registro
    var primerNombre by remember { mutableStateOf("") }
    var segundoNombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var runRegister by remember { mutableStateOf("") }
    var dvRegister by remember { mutableStateOf("") }
    var emailRegister by remember { mutableStateOf("") }
    var telefonoRegister by remember { mutableStateOf("") }
    var passwordRegister by remember { mutableStateOf("") }
    var showPasswordRegister by remember { mutableStateOf(false) }

    // Fecha de nacimiento
    var anioNacimiento by remember { mutableStateOf("") }
    var mesNacimiento by remember { mutableStateOf("") }
    var diaNacimiento by remember { mutableStateOf("") }

    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sanaesencia",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isLoginMode) "Inicia sesión con tu RUT" else "Crea tu cuenta de paciente",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs
            TabRow(
                selectedTabIndex = if (isLoginMode) 0 else 1,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                indicator = {}
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

            // Error
            if (errorMensaje != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(16.dp)
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

            // --- FORMULARIO ---
            if (isLoginMode) {
                LoginForm(
                    rutLogin = rutLogin, onRutChange = { rutLogin = it.filter(Char::isDigit).take(8) },
                    dvLogin = dvLogin, onDvChange = { dvLogin = it.uppercase().take(1).filter { c -> c.isDigit() || c == 'K' } },
                    password = passwordLogin, onPasswordChange = { passwordLogin = it },
                    showPassword = showPasswordLogin, onTogglePassword = { showPasswordLogin = !showPasswordLogin }
                )
            } else {
                RegisterForm(
                    primerNombre = primerNombre, onPrimerNombre = { primerNombre = it },
                    segundoNombre = segundoNombre, onSegundoNombre = { segundoNombre = it },
                    apellidoPaterno = apellidoPaterno, onApellidoPaterno = { apellidoPaterno = it },
                    apellidoMaterno = apellidoMaterno, onApellidoMaterno = { apellidoMaterno = it },
                    run = runRegister, onRun = { runRegister = it.filter(Char::isDigit).take(8) },
                    dv = dvRegister, onDv = { dvRegister = it.uppercase().take(1).filter { c -> c.isDigit() || c == 'K' } },
                    email = emailRegister, onEmail = { emailRegister = it },
                    telefono = telefonoRegister, onTelefono = { telefonoRegister = it.filter(Char::isDigit).take(9) },
                    password = passwordRegister, onPassword = { passwordRegister = it },
                    showPassword = showPasswordRegister, onTogglePassword = { showPasswordRegister = !showPasswordRegister },
                    anio = anioNacimiento, onAnio = { anioNacimiento = it },
                    mes = mesNacimiento, onMes = { mesNacimiento = it },
                    dia = diaNacimiento, onDia = { diaNacimiento = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón
            Button(
                onClick = {
                    errorMensaje = null
                    if (isLoginMode) {
                        // Validar login
                        if (rutLogin.isBlank()) {
                            errorMensaje = "Ingresa tu RUT"
                            return@Button
                        }
                        if (dvLogin.isBlank()) {
                            errorMensaje = "Ingresa el dígito verificador"
                            return@Button
                        }
                        if (passwordLogin.isBlank()) {
                            errorMensaje = "Ingresa tu contraseña"
                            return@Button
                        }
                        isLoading = true
                        coroutineScope.launch(Dispatchers.IO) {
                            val paciente = ServiceLocator.pacienteRepository.getPacienteByRun(rutLogin)
                            if (paciente == null) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    errorMensaje = "RUT no registrado."
                                }
                            } else if (paciente.runDv != dvLogin) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    errorMensaje = "Dígito verificador incorrecto."
                                }
                            } else if (paciente.passwordHash.isNotEmpty() && paciente.passwordHash != hashPassword(passwordLogin)) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    errorMensaje = "Contraseña incorrecta."
                                }
                            } else {
                                ServiceLocator.pacienteRepository.setPacienteActual(paciente.id)
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    onLoginSuccess()
                                }
                            }
                        }
                    } else {
                        // Validar registro
                        if (primerNombre.isBlank() || apellidoPaterno.isBlank() || apellidoMaterno.isBlank()) {
                            errorMensaje = "Completa nombre y apellidos"
                            return@Button
                        }
                        if (runRegister.isBlank() || dvRegister.isBlank()) {
                            errorMensaje = "Completa el RUT"
                            return@Button
                        }
                        if (emailRegister.isBlank()) {
                            errorMensaje = "Ingresa tu correo"
                            return@Button
                        }
                        if (telefonoRegister.length != 9) {
                            errorMensaje = "El teléfono debe tener 9 dígitos"
                            return@Button
                        }
                        if (passwordRegister.length < 4) {
                            errorMensaje = "La contraseña debe tener al menos 4 caracteres"
                            return@Button
                        }
                        if (anioNacimiento.isBlank() || mesNacimiento.isBlank() || diaNacimiento.isBlank()) {
                            errorMensaje = "Selecciona tu fecha de nacimiento"
                            return@Button
                        }

                        val anioInt = anioNacimiento.toIntOrNull() ?: 0
                        val mesInt = mesNacimiento.toIntOrNull() ?: 0
                        val diaInt = diaNacimiento.toIntOrNull() ?: 0

                        if (!esFechaValida(diaInt, mesInt, anioInt)) {
                            errorMensaje = "Fecha de nacimiento inválida"
                            return@Button
                        }

                        isLoading = true
                        coroutineScope.launch(Dispatchers.IO) {
                            val existente = ServiceLocator.pacienteRepository.getPacienteByRun(runRegister)
                            if (existente != null) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    errorMensaje = "Este RUT ya está registrado."
                                }
                            } else {
                                val fecha = "%04d-%02d-%02d".format(anioInt, mesInt, diaInt)
                                val paciente = Paciente(
                                    id = 0,
                                    run = runRegister,
                                    runDv = dvRegister,
                                    primerNombre = primerNombre.trim(),
                                    segundoNombre = segundoNombre.trim().ifBlank { null },
                                    apellidoPaterno = apellidoPaterno.trim(),
                                    apellidoMaterno = apellidoMaterno.trim(),
                                    email = emailRegister.trim(),
                                    telefono = telefonoRegister,
                                    fechaNacimiento = fecha,
                                    passwordHash = hashPassword(passwordRegister)
                                )
                                ServiceLocator.pacienteRepository.updatePaciente(paciente)
                                val registrado = ServiceLocator.pacienteRepository.getPacienteByRun(runRegister)
                                if (registrado != null) {
                                    ServiceLocator.pacienteRepository.setPacienteActual(registrado.id)
                                }
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    onLoginSuccess()
                                }
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

@Composable
private fun LoginForm(
    rutLogin: String, onRutChange: (String) -> Unit,
    dvLogin: String, onDvChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    showPassword: Boolean, onTogglePassword: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = rutLogin,
            onValueChange = onRutChange,
            label = { Text("RUT") },
            placeholder = { Text("12.345.678") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = RutVisualTransformation()
        )
        OutlinedTextField(
            value = dvLogin,
            onValueChange = onDvChange,
            label = { Text("DV") },
            placeholder = { Text("K") },
            modifier = Modifier.width(80.dp),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Contraseña") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onTogglePassword) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (showPassword) "Ocultar" else "Mostrar"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterForm(
    primerNombre: String, onPrimerNombre: (String) -> Unit,
    segundoNombre: String, onSegundoNombre: (String) -> Unit,
    apellidoPaterno: String, onApellidoPaterno: (String) -> Unit,
    apellidoMaterno: String, onApellidoMaterno: (String) -> Unit,
    run: String, onRun: (String) -> Unit,
    dv: String, onDv: (String) -> Unit,
    email: String, onEmail: (String) -> Unit,
    telefono: String, onTelefono: (String) -> Unit,
    password: String, onPassword: (String) -> Unit,
    showPassword: Boolean, onTogglePassword: () -> Unit,
    anio: String, onAnio: (String) -> Unit,
    mes: String, onMes: (String) -> Unit,
    dia: String, onDia: (String) -> Unit
) {
    OutlinedTextField(
        value = primerNombre,
        onValueChange = onPrimerNombre,
        label = { Text("Primer Nombre") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = segundoNombre,
        onValueChange = onSegundoNombre,
        label = { Text("Segundo Nombre (opcional)") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = apellidoPaterno,
            onValueChange = onApellidoPaterno,
            label = { Text("Apellido Paterno") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = apellidoMaterno,
            onValueChange = onApellidoMaterno,
            label = { Text("Apellido Materno") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
    }
    Spacer(modifier = Modifier.height(12.dp))

    // RUT + DV
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = run,
            onValueChange = onRun,
            label = { Text("RUT") },
            placeholder = { Text("12.345.678") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = RutVisualTransformation()
        )
        OutlinedTextField(
            value = dv,
            onValueChange = onDv,
            label = { Text("DV") },
            placeholder = { Text("K") },
            modifier = Modifier.width(80.dp),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
    }
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = email,
        onValueChange = onEmail,
        label = { Text("Correo electrónico") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
    Spacer(modifier = Modifier.height(12.dp))

    // Teléfono con +56
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.height(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("+56", fontWeight = FontWeight.Bold)
            }
        }
        OutlinedTextField(
            value = telefono,
            onValueChange = onTelefono,
            label = { Text("Teléfono") },
            placeholder = { Text("912345678") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
    Spacer(modifier = Modifier.height(12.dp))

    // Fecha de nacimiento: Año + Mes en fila 1, Día en fila 2
    Text("Fecha de nacimiento", style = MaterialTheme.typography.labelLarge)
    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Año
        var anioExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = anioExpanded,
            onExpandedChange = { anioExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = anio,
                onValueChange = {},
                readOnly = true,
                label = { Text("Año") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = anioExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(16.dp)
            )
            ExposedDropdownMenu(expanded = anioExpanded, onDismissRequest = { anioExpanded = false }) {
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                (currentYear downTo currentYear - 100).forEach { y ->
                    DropdownMenuItem(
                        text = { Text(y.toString()) },
                        onClick = {
                            onAnio(y.toString())
                            anioExpanded = false
                        }
                    )
                }
            }
        }

        // Mes
        var mesExpanded by remember { mutableStateOf(false) }
        val meses = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        ExposedDropdownMenuBox(
            expanded = mesExpanded,
            onExpandedChange = { mesExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = if (mes.isBlank()) "" else meses.getOrElse(mes.toInt() - 1) { "" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Mes") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mesExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(16.dp)
            )
            ExposedDropdownMenu(expanded = mesExpanded, onDismissRequest = { mesExpanded = false }) {
                meses.forEachIndexed { index, nombre ->
                    DropdownMenuItem(
                        text = { Text(nombre) },
                        onClick = {
                            onMes((index + 1).toString())
                            mesExpanded = false
                        }
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Día (fila separada para que quepa)
    var diaExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = diaExpanded,
        onExpandedChange = { diaExpanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = dia,
            onValueChange = {},
            readOnly = true,
            label = { Text("Día") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = diaExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(16.dp)
        )
        ExposedDropdownMenu(expanded = diaExpanded, onDismissRequest = { diaExpanded = false }) {
            val maxDia = diasEnMes(mes.toIntOrNull() ?: 1, anio.toIntOrNull() ?: 2000)
            (1..maxDia).forEach { d ->
                DropdownMenuItem(
                    text = { Text(d.toString()) },
                    onClick = {
                        onDia(d.toString())
                        diaExpanded = false
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    // Contraseña
    OutlinedTextField(
        value = password,
        onValueChange = onPassword,
        label = { Text("Contraseña") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onTogglePassword) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (showPassword) "Ocultar" else "Mostrar"
                )
            }
        }
    )
}

/** Valida si una fecha es real (evita 29 feb en año no bisiesto, 30 feb, etc.). */
private fun esFechaValida(dia: Int, mes: Int, anio: Int): Boolean {
    if (anio < 1900 || anio > 2100) return false
    if (mes < 1 || mes > 12) return false
    val maxDia = diasEnMes(mes, anio)
    return dia in 1..maxDia
}

/** Devuelve los días máximos del mes considerando años bisiestos. */
private fun diasEnMes(mes: Int, anio: Int): Int = when (mes) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (esBisiesto(anio)) 29 else 28
    else -> 30
}

/** Determina si un año es bisiesto. */
private fun esBisiesto(anio: Int): Boolean =
    (anio % 4 == 0 && anio % 100 != 0) || (anio % 400 == 0)
