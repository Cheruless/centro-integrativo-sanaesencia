package cl.sanaesencia.app.domain.model

import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Bloque de atención de 30 minutos dentro de la jornada 09:00–18:00.
 * Una Cita ocupa 1 o 2 bloques según [Especialidad.bloquesPorSesion].
 * @author Exequiel
 */
data class Horario(val inicio: LocalTime) {

    val fin: LocalTime
        get() = inicio.plusMinutes(BLOQUE_MINUTOS)

    /** Etiqueta de UI en formato 24h: "09:00 hrs". */
    val etiqueta: String
        get() = "${inicio.format(FORMATO_HORA)} hrs"

    companion object {
        const val BLOQUE_MINUTOS: Long = 30
        val INICIO_JORNADA: LocalTime = LocalTime.of(9, 0)
        val FIN_JORNADA: LocalTime = LocalTime.of(18, 0)
        val FORMATO_HORA: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        /** 18 bloques: 09:00, 09:30, … 17:30. */
        const val TOTAL_BLOQUES: Int = 18

        /** Todos los inicios de bloque del día. */
        fun bloquesDelDia(): List<Horario> =
            (0 until TOTAL_BLOQUES).map { Horario(INICIO_JORNADA.plusMinutes(it * BLOQUE_MINUTOS)) }

        /**
         * Inicios válidos para una sesión de [bloquesPorSesion] bloques.
         * Una sesión de 2 bloques debe empezar a más tardar 17:00 (termina 18:00).
         */
        fun horasValidasParaSesion(bloquesPorSesion: Int): List<Horario> =
            bloquesDelDia().take(TOTAL_BLOQUES - bloquesPorSesion + 1)

        /** Índice 0-based del bloque que empieza en [hora]; -1 si está fuera de jornada. */
        fun indiceDe(hora: LocalTime): Int {
            val mins = Duration.between(INICIO_JORNADA, hora).toMinutes()
            return if (mins < 0 || mins % BLOQUE_MINUTOS != 0L || mins >= TOTAL_BLOQUES * BLOQUE_MINUTOS) -1
            else (mins / BLOQUE_MINUTOS).toInt()
        }
    }
}
