package cl.sanaesencia.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import cl.sanaesencia.app.domain.model.Paciente

/** Consultas para la tabla de pacientes. */
@Dao
interface PacienteDao {
    @Query("SELECT * FROM pacientes LIMIT 1")
    suspend fun getActual(): Paciente?

    @Query("SELECT * FROM pacientes WHERE rut = :rut")
    suspend fun getByRut(rut: String): Paciente?

    @Upsert
    suspend fun update(paciente: Paciente)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paciente: Paciente)
}
