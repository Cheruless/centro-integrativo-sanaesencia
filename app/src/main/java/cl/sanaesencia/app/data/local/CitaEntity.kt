package cl.sanaesencia.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import cl.sanaesencia.app.domain.model.Especialidad
import cl.sanaesencia.app.domain.model.EstadoCita
import java.time.LocalDate
import java.time.LocalTime

/**
 * Entidad Room para citas. Guarda los ids de especialista y paciente
 * como Foreign Keys en vez de objetos anidados.
 */
@Entity(
    tableName = "citas",
    foreignKeys = [
        ForeignKey(
            entity = cl.sanaesencia.app.domain.model.Especialista::class,
            parentColumns = ["id"],
            childColumns = ["especialistaId"]
        ),
        ForeignKey(
            entity = cl.sanaesencia.app.domain.model.Paciente::class,
            parentColumns = ["id"],
            childColumns = ["pacienteId"]
        )
    ]
)
data class CitaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fecha: LocalDate,
    val hora: LocalTime,
    val especialidad: Especialidad,
    val precio: Double,
    val notas: String? = null,
    val especialistaId: Int,
    val pacienteId: Int,
    val estado: EstadoCita = EstadoCita.PENDIENTE
)
