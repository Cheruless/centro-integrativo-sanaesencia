package cl.sanaesencia.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.model.ItemCarrito
import cl.sanaesencia.app.domain.model.Paciente
import cl.sanaesencia.app.domain.model.Producto

@Database(
    entities = [Especialista::class, Producto::class, Paciente::class, CitaEntity::class, ItemCarrito::class, SesionEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SanaesenciaDatabase : RoomDatabase() {

    abstract val especialistaDao: EspecialistaDao
    abstract val productoDao: ProductoDao
    abstract val pacienteDao: PacienteDao
    abstract val citaDao: CitaDao
    abstract val sesionDao: SesionDao
    abstract fun carritoDao(): CarritoDao
}
