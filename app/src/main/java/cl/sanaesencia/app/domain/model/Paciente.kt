package cl.sanaesencia.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Paciente registrado en Sanaesencia. Contiene datos personales
 * y de contacto necesarios para agendar citas.
 */
@Entity(tableName = "pacientes")
data class Paciente(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
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
