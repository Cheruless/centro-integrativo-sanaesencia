package cl.sanaesencia.app.domain.model

import java.time.DayOfWeek

/**
 * Profesional del centro. Puede ejercer una o más especialidades
 * y atiende en un conjunto fijo de días de la semana.
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
    /** Nombre completo: "Camila Andrea Silva Morales". */
    val nombreCompleto: String
        get() = listOfNotNull(primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    /** Nombre corto para UI: "Camila Silva Morales". */
    val nombreCorto: String
        get() = "$primerNombre $apellidoPaterno $apellidoMaterno"

    /** Especialidades como texto: "Psicología, Terapia Floral". */
    val especialidadesLabel: String
        get() = especialidades.joinToString(", ") { it.displayName }

    /** Indica si el profesional atiende el día de la semana dado. */
    fun atiendeEn(dia: DayOfWeek): Boolean = dia in diasAtencion
}
