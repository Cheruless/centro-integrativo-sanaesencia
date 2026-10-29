package cl.sanaesencia.app.ui.carrito

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
import cl.sanaesencia.app.data.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Pantalla de procesamiento de pago del carrito (mock MVP).
 * Simula 3 segundos, descuenta stock y vacía el carrito.
 * Patrón: Transacción (stock + carrito) + Observer (Flow notifica vaciado).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcesandoPagoCarritoScreen(
    onPagoCompletado: () -> Unit = {}
) {
    var estado by remember { mutableStateOf("procesando") } // procesando, exito, error
    var mensajeError by remember { mutableStateOf("") }

    BackHandler(enabled = estado == "procesando") { /* no-op */ }

    LaunchedEffect(Unit) {
        delay(3_000)
        val exito = withContext(Dispatchers.IO) {
            ServiceLocator.carritoRepository.comprarProductos()
        }
        estado = if (exito) "exito" else "error"
        if (!exito) {
            mensajeError = "No hay stock suficiente para completar la compra."
        }
        if (exito) {
            delay(1_500)
            onPagoCompletado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Procesando pago") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (estado) {
                "procesando" -> {
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
                            text = "Descontando stock y confirmando tu compra",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                "exito" -> {
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
                            text = "¡Compra realizada con éxito!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Los productos fueron descontados del stock",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                "error" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "Error en la compra",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = mensajeError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = onPagoCompletado) {
                            Text("Volver al carrito")
                        }
                    }
                }
            }
        }
    }
}
