package cl.sanaesencia.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import cl.sanaesencia.app.domain.model.Especialista

@Database(
    entities = [Especialista::class],
    version = 1,
    exportSchema = false
)
abstract class SanaesenciaDatabase : RoomDatabase() {

    // Aquí le decimos a la base de datos qué DAOs contiene
    abstract val especialistaDao: EspecialistaDao

}