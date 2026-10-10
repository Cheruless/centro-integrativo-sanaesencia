package cl.sanaesencia.app.domain.model

data class Paciente(
    val id: Int,
    val rut: String, // INVESTIGAR LEY DE PROTECCIÓN DE DATOS. NO CREO QUE PODAMOS USAR EL RUT
    val primerNombre: String,
    val segundoNombre: String? = null,
    val apellidoPaterno: String,
    val apellidoMaterno: String,

    val email: String,
    val telefono: String,
    val fechaNacimiento: String? = null // CONSIDERAR USO DE DATE, SOLO INTERESA LA FECHA, NO HORAS.
) {
    val nombreCompleto: String
        get() = listOfNotNull(primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    val nombreCorto: String
        get() = "$primerNombre $apellidoPaterno $apellidoMaterno"
}