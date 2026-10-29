package cl.sanaesencia.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Paciente registrado en Sanaesencia. Contiene datos personales
 * y de contacto necesarios para agendar citas y comprar productos.
 * El RUT se guarda como run (sin puntos) + dígito verificador.
 */
@Entity(tableName = "pacientes")
data class Paciente(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val run: String,
    val runDv: String,
    val primerNombre: String,
    val segundoNombre: String? = null,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val email: String,
    val telefono: String,
    val fechaNacimiento: String? = null,
    val direccion: String? = null,
    val fotoUri: String? = null,
    val passwordHash: String = ""
) {
    /** Nombre completo del paciente. */
    val nombreCompleto: String
        get() = listOfNotNull(primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    /** Nombre corto para UI: "Exequiel Pulgar Cerda". */
    val nombreCorto: String
        get() = "$primerNombre $apellidoPaterno $apellidoMaterno"

    /** RUT formateado para mostrar: "20.781.681-7". */
    val rutFormateado: String
        get() = formatearRut(run, runDv)

    companion object {
        /** Formatea run+dvh a "12.345.678-K". */
        fun formatearRut(run: String, dv: String): String {
            val reversed = run.reversed()
            val formatted = reversed.chunked(3).joinToString(".").reversed()
            return "$formatted-$dv"
        }

        /** Quita puntos y guión: "20.781.681-7" → run="20781681", dv="7". */
        fun parseRut(rutCompleto: String): Pair<String, String> {
            val limpio = rutCompleto.replace(".", "").replace("-", "").trim()
            val run = limpio.dropLast(1)
            val dv = limpio.last().toString().uppercase()
            return run to dv
        }
    }
}
