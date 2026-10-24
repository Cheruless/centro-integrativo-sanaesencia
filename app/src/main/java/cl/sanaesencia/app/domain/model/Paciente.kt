package cl.sanaesencia.app.domain.model

/**
 * Paciente registrado en Sanaesencia. Contiene datos personales
 * y de contacto necesarios para agendar citas.
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
    /** Nombre completo del paciente. */
    val nombreCompleto: String
        get() = listOfNotNull(primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    /** Nombre corto para UI: "Exequiel Pulgar Cerda". */
    val nombreCorto: String
        get() = "$primerNombre $apellidoPaterno $apellidoMaterno"
}
