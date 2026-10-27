package cl.sanaesencia.app.ui.citas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Pantalla de procesamiento de pago (mock MVP).
 * Simula 3 segundos de procesamiento y muestra confirmación.
 * No implementa pago real — solo demostración de flujo.
 * @author Exequiel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcesandoPagoScreen(
    viewModel: CitaViewModel,
    citaId: Int,
    onPagoCompletado: () -> Unit = {}
) {
    val pagoState by viewModel.pagoState.collectAsState()

    // Bloquear retroceso mientras se procesa
    BackHandler(enabled = pagoState.estado == EstadoPago.PROCESANDO) {
        // No-op: no se puede volver durante el procesamiento
    }

    // Siempre iniciar de cero al entrar: limpia estado residual de un pago anterior
    // y asegura que se procese la cita actual.
    LaunchedEffect(citaId) {
        viewModel.iniciarPago(citaId)
        delay(3_000)
        viewModel.completarPago()
        delay(1_500)
        onPagoCompletado()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Procesando pago") },
                navigationIcon = {
                    if (pagoState.estado != EstadoPago.PROCESANDO) {
                        IconButton(onClick = onPagoCompletado) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Cerrar"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (pagoState.estado) {
                EstadoPago.PROCESANDO -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            strokeWidth = 5.dp
                        )
                        Text(
                            text = "Procesando pago...",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Esto tomará unos segundos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                EstadoPago.EXITOSO -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Se ha confirmado tu cita exitosamente",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Tu cita ha sido confirmada y pagada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                EstadoPago.INACTIVO -> {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
