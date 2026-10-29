package cl.sanaesencia.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sesion")
data class SesionEntity(
    @PrimaryKey
    val id: Int = 1,
    val pacienteId: Int
)
