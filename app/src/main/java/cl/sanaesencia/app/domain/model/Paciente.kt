package cl.sanaesencia.app.domain.model

/**
 * Representa la información personal de un paciente registrado en Sanaesencia.
 *
 * TODO (Módulo Pacientes / Perfil):
 *  1. Investigar la Ley N° 19.628 sobre protección de la vida privada en Chile
 *     para asegurar el tratamiento correcto de datos sensibles (RUT, historial médico).
 *  2. Considerar el uso de 'LocalDate' para la fecha de nacimiento en lugar de String
 *     si se requiere calcular la edad o validar rangos.
 */
data class Paciente(
    val id: Int,
    val rut: String,
    val primerNombre: String,
    val segundoNombre: String? = null,
    val apellidoPaterno: String,
    val apellidoMaterno: String,

    val email: String,
    val telefono: String,
    val fechaNacimiento: String? = null
) {
    val nombreCompleto: String
        get() = listOfNotNull(primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    val nombreCorto: String
        get() = "$primerNombre $apellidoPaterno $apellidoMaterno"
}