package cl.sanaesencia.app.domain.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** Estados posibles de una cita médica. */
enum class EstadoCita {
    PENDIENTE,
    CONFIRMADA,
    REALIZADA,
    CANCELADA
}

/**
 * Cita agendada por un paciente. La duración se hereda
 * de la especialidad (1 o 2 bloques de 30 min).
 */
data class Cita(
    val id: Int,
    val fecha: LocalDate,
    val hora: LocalTime,
    val especialidad: Especialidad,
    val precio: Double,
    val notas: String? = null,
    val especialista: Especialista,
    val paciente: Paciente,
    val estado: EstadoCita = EstadoCita.PENDIENTE
) {
    /** Nombre del servicio (equivale a `especialidad.displayName`). */
    val servicio: String
        get() = especialidad.displayName

    /** Bloques de 30 min que ocupa la cita (1 o 2). */
    val bloques: Int
        get() = especialidad.bloquesPorSesion

    /** Hora de término de la sesión. */
    val horaFin: LocalTime
        get() = hora.plusMinutes(especialidad.duracionMinutos.toLong())

    /** Fecha en formato ISO: "2026-10-15". */
    val fechaLabel: String
        get() = fecha.toString()

    /** Hora en formato 24h: "10:30". */
    val horaLabel: String
        get() = hora.format(Horario.FORMATO_HORA)

    companion object {
        /** Fecha corta: "15/10" (mismo año) o "08/01/27" (año distinto). */
        fun fechaCorta(fecha: LocalDate, hoy: LocalDate = LocalDate.now()): String =
            fecha.format(
                DateTimeFormatter.ofPattern(if (fecha.year != hoy.year) "dd/MM/yy" else "dd/MM")
            )

        /** Mensaje al agendar sin pago: "Cita agendada para el día 15/10. …" */
        fun mensajeCitaAgendada(fecha: LocalDate): String =
            "Cita agendada para el día ${fechaCorta(fecha)}. Puedes proceder con el pago pendiente."
    }
}
