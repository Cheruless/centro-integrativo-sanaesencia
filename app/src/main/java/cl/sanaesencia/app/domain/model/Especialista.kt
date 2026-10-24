package cl.sanaesencia.app.domain.model

import java.time.DayOfWeek

/**
 * Representa a un profesional o especialista que imparte atenciones en Sanaesencia.
 * Puede ejercer una o más especialidades y atiende en un conjunto fijo de días
 * de la semana (jornada 09:00–18:00).
 * @author Exequiel
 */
data class Especialista(
    val id: Int,
    val primerNombre: String,
    val segundoNombre: String? = null,
    val apellidoPaterno: String,
    val apellidoMaterno: String,

    val especialidades: List<Especialidad>,
    val diasAtencion: Set<DayOfWeek>,
    val descripcion: String = "",
    val email: String = "",
    val telefono: String = "",
    val imagenResId: Int? = null
) {
    val nombreCompleto: String
        get() = listOfNotNull(primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    val nombreCorto: String
        get() = "$primerNombre $apellidoPaterno $apellidoMaterno"

    /** Etiqueta de UI: "Psicología, Terapia Floral". */
    val especialidadesLabel: String
        get() = especialidades.joinToString(", ") { it.displayName }

    fun atiendeEn(dia: DayOfWeek): Boolean = dia in diasAtencion
}
