package cl.sanaesencia.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.sanaesencia.app.domain.model.Especialista
import kotlinx.coroutines.flow.Flow

@Dao
interface EspecialistaDao {

    // Obtiene todos los especialistas de la tabla en tiempo real
    @Query("SELECT * FROM especialistas")
    fun obtenerEspecialistas(): Flow<List<Especialista>>

    // Busca un especialista específico por su ID
    @Query("SELECT * FROM especialistas WHERE id = :id")
    suspend fun obtenerEspecialistaPorId(id: Int): Especialista?

    // Inserta un nuevo especialista. Si ya existe uno con el mismo ID, lo reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEspecialista(especialista: Especialista)

    // Elimina un especialista
    @Delete
    suspend fun eliminarEspecialista(especialista: Especialista)
}