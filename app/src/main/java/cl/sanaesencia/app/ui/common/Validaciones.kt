package cl.sanaesencia.app.ui.common

import android.util.Patterns
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** Reglas del centro: horario de atención y política de cancelación. */
object Politicas {
    const val HORAS_MIN_CANCELACION = 24L
    val HORA_APERTURA: LocalTime = LocalTime.of(9, 0)
    val HORA_CIERRE: LocalTime = LocalTime.of(19, 0)
}

object Validaciones {

    fun parseFecha(f: String): LocalDate? = try {
        LocalDate.parse(f)
    } catch (e: Exception) {
        null
    }

    fun parseHora(h: String): LocalTime? = try {
        LocalTime.parse(h)
    } catch (e: Exception) {
        null
    }

    fun validarFecha(fecha: String): String? {
        if (fecha.isBlank()) return "Selecciona la fecha de la cita."
        val d = parseFecha(fecha) ?: return "La fecha no es válida."
        if (d.isBefore(LocalDate.now())) return "No puedes agendar en una fecha pasada."
        if (d.dayOfWeek == DayOfWeek.SUNDAY) return "El centro no atiende los domingos."
        return null
    }

    fun validarHora(fecha: String, hora: String): String? {
        if (hora.isBlank()) return "Selecciona la hora de la cita."
        val h = parseHora(hora) ?: return "La hora no es válida."
        if (h.isBefore(Politicas.HORA_APERTURA) || !h.isBefore(Politicas.HORA_CIERRE)) {
            return "Horario de atención: 09:00 a 19:00."
        }
        val d = parseFecha(fecha)
        if (d != null && !LocalDateTime.of(d, h).isAfter(LocalDateTime.now())) {
            return "Esa hora ya pasó."
        }
        return null
    }

    /** Minutos que faltan para la cita, o null si la fecha/hora no se pueden leer. */
    fun minutosHasta(fecha: String, hora: String): Long? {
        val d = parseFecha(fecha) ?: return null
        val h = parseHora(hora) ?: return null
        return Duration.between(LocalDateTime.now(), LocalDateTime.of(d, h)).toMinutes()
    }

    fun validarEmail(email: String): String? = when {
        email.isBlank() -> "Ingresa tu correo."
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
            "Correo no válido (ej: nombre@correo.cl)."
        else -> null
    }

    fun validarTelefono(telefono: String): String? {
        val digitos = telefono.filter { it.isDigit() }
        return when {
            telefono.isBlank() -> "Ingresa tu teléfono."
            (digitos.length == 9 && digitos.startsWith("9")) ||
                (digitos.length == 11 && digitos.startsWith("569")) -> null
            else -> "Teléfono no válido (ej: +56 9 1234 5678)."
        }
    }
}
