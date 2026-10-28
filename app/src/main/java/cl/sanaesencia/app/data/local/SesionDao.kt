package cl.sanaesencia.app.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SesionDao {
    @Query("SELECT pacienteId FROM sesion WHERE id = 1")
    suspend fun getActivePacienteId(): Int?

    @Upsert
    suspend fun setSession(sesion: SesionEntity)
}
