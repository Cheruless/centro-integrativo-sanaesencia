package cl.sanaesencia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Calendario mensual interactivo (mes completo, lunes primero).
 * Día seleccionado resaltado; punto indica disponibilidad.
 * @author Exequiel
 */
@Composable
fun MonthCalendar(
    mes: YearMonth,
    fechaSeleccionada: LocalDate?,
    diasConDisponibilidad: Set<LocalDate>,
    onMesChange: (YearMonth) -> Unit,
    onFechaSeleccionada: (LocalDate) -> Unit,
    hoy: LocalDate = LocalDate.now(),
    modifier: Modifier = Modifier
) {
    val locale = Locale.forLanguageTag("es-CL")
    val primerDia = mes.atDay(1)
    val leadingBlanks = primerDia.dayOfWeek.value - 1 // MONDAY=1 → 0 blanks
    val totalDias = mes.lengthOfMonth()
    val totalCeldas = leadingBlanks + totalDias

    Column(modifier = modifier.fillMaxWidth()) {
        // Header: mes + navegación
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onMesChange(mes.minusMonths(1)) }) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Mes anterior"
                )
            }
            Text(
                text = mes.month.getDisplayName(TextStyle.FULL, locale)
                    .replaceFirstChar { it.uppercase(locale) } + " " + mes.year,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onMesChange(mes.plusMonths(1)) }) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Mes siguiente"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Encabezado de días de la semana (lunes primero)
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do").forEach { dia ->
                Text(
                    text = dia,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Grilla de días (Column de Rows — evitar Lazy* dentro de verticalScroll)
        val filas = (totalCeldas + 6) / 7
        for (fila in 0 until filas) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val index = fila * 7 + col
                    if (index < leadingBlanks || index >= totalCeldas) {
                        Spacer(modifier = Modifier.weight(1f).height(44.dp))
                    } else {
                        val dia = primerDia.plusDays((index - leadingBlanks).toLong())
                        val enabled = !dia.isBefore(hoy)
                        val selected = dia == fechaSeleccionada
                        val conDisponibilidad = dia in diasConDisponibilidad

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primary
                                        else androidx.compose.ui.graphics.Color.Transparent
                                    )
                                    .then(
                                        if (enabled) Modifier.clickable { onFechaSeleccionada(dia) }
                                        else Modifier
                                    ),
                            ) {
                                Text(
                                    text = dia.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        selected -> MaterialTheme.colorScheme.onPrimary
                                        !enabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                if (!selected && conDisponibilidad && enabled) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
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
