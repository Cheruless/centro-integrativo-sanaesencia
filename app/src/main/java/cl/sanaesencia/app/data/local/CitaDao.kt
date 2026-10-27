package cl.sanaesencia.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Consultas para la tabla de citas. */
@Dao
interface CitaDao {
    @Query("SELECT * FROM citas")
    fun observarTodas(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas")
    suspend fun getAll(): List<CitaEntity>

    @Query("SELECT * FROM citas WHERE id = :id")
    suspend fun getById(id: Int): CitaEntity?

    @Insert
    suspend fun insert(cita: CitaEntity): Long

    @Query("UPDATE citas SET estado = 'CANCELADA' WHERE id = :id")
    suspend fun cancelar(id: Int): Int

    @Query("UPDATE citas SET estado = 'CONFIRMADA' WHERE id = :id AND estado = 'PENDIENTE'")
    suspend fun confirmar(id: Int): Int

    @Query("UPDATE citas SET estado = 'CONFIRMADA' WHERE id = :id AND estado = 'PENDIENTE'")
    suspend fun pagar(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(citas: List<CitaEntity>)
}
