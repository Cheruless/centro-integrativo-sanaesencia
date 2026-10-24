package cl.sanaesencia.app.domain.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class EstadoCita {
    PENDIENTE,
    CONFIRMADA,
    REALIZADA,
    CANCELADA
}

/**
 * Cita médica/terapéutica. La duración se hereda de [especialidad.bloquesPorSesion].
 * @author Exequiel
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
    /** Nombre de servicio derivado para UI (equivale al antiguo campo `servicio`). */
    val servicio: String
        get() = especialidad.displayName

    val bloques: Int
        get() = especialidad.bloquesPorSesion

    val horaFin: LocalTime
        get() = hora.plusMinutes(especialidad.duracionMinutos.toLong())

    /** Fecha legible ISO: "2026-10-15". */
    val fechaLabel: String
        get() = fecha.toString()

    /** Hora legible 24h: "10:30". */
    val horaLabel: String
        get() = hora.format(Horario.FORMATO_HORA)

    companion object {
        /**
         * Fecha corta "dd/MM" si es del año en curso; "dd/MM/yy" si es de otro año.
         * @author Exequiel
         */
        fun fechaCorta(fecha: LocalDate, hoy: LocalDate = LocalDate.now()): String =
            fecha.format(
                DateTimeFormatter.ofPattern(if (fecha.year != hoy.year) "dd/MM/yy" else "dd/MM")
            )

        /**
         * Mensaje de notificación al agendar sin pago inmediato.
         * Ej: "Cita agendada para el día 15/10. Puedes proceder con el pago pendiente."
         * @author Exequiel
         */
        fun mensajeCitaAgendada(fecha: LocalDate): String =
            "Cita agendada para el día ${fechaCorta(fecha)}. Puedes proceder con el pago pendiente."
    }
}
