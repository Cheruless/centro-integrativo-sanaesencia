package cl.sanaesencia.app.ui.common

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatos {
    private val localeCL: Locale = Locale.forLanguageTag("es-CL")

    fun precio(valor: Double): String =
        "$" + NumberFormat.getIntegerInstance(localeCL).format(valor.toLong())

    fun fecha(iso: String): String = try {
        LocalDate.parse(iso).format(DateTimeFormatter.ofPattern("EEE d MMM yyyy", localeCL))
    } catch (e: Exception) {
        iso
    }
}
