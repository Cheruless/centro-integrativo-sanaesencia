package cl.sanaesencia.app.domain.model

/**
 * Representa a un profesional o especialista que imparte atenciones en Sanaesencia.
 *
 * TODO (Módulo Especialistas):
 *  1. Desarrollar una data class o Enum 'Especialidad' si un profesional maneja
 *     múltiples especialidades (List<Especialidad>).
 *  2. Especialidades impartidas por el centro integrativo:
 *      - Psicología
 *      - Terapia Ocupacional
 *      - Fonoaudiología
 *      - Psicopedagogía
 *      - Nutrición
 *      - Matrona
 *      - Quiropraxia
 *      - Masoterapia / Masajes
 *      - Evaluaciones del Neurodesarrollo
 *      - Terapia Floral
 *      - Aromaterapia
 */
data class Especialista(
    val id: Int,
    val primerNombre: String,
    val segundoNombre: String? = null,
    val apellidoPaterno: String,
    val apellidoMaterno: String,

    val especialidad: String,
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
}