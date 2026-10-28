package cl.sanaesencia.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val fechaNacimiento: String? = null,
    val fotoUri: String? = null
) {
    val nombreCompleto: String
        get() = listOfNotNull(primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    val nombreCorto: String
        get() = "$primerNombre $apellidoPaterno $apellidoMaterno"
}
