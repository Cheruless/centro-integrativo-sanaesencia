package cl.sanaesencia.app.data

import android.content.Context
import androidx.room.Room
import cl.sanaesencia.app.data.local.SanaesenciaDatabase
import cl.sanaesencia.app.data.local.SeedCallback
import cl.sanaesencia.app.data.repository.*
import cl.sanaesencia.app.domain.repository.*

/**
 * Proveedor simple de repositorios. Inicializa la base de datos Room
 * una sola vez y crea los repositorios que inyectan los ViewModels.
 */
object ServiceLocator {

    private lateinit var database: SanaesenciaDatabase

    /** Inicializa la base de datos. Llamar una vez desde MainActivity o Application. */
    fun init(context: Context) {
        if (!::database.isInitialized) {
            database = Room.databaseBuilder(
                context.applicationContext,
                SanaesenciaDatabase::class.java,
                "sanaesencia.db"
            )
                .addCallback(SeedCallback())
                .build()
        }
    }

    val productoRepository: ProductoRepository by lazy {
        RoomProductoRepository(database.productoDao)
    }

    val pacienteRepository: PacienteRepository by lazy {
        RoomPacienteRepository(database.pacienteDao)
    }

    val especialistaRepository: EspecialistaRepository by lazy {
        RoomEspecialistaRepository(database.especialistaDao)
    }

    val citaRepository: CitaRepository by lazy {
        RoomCitaRepository(database.citaDao, database.especialistaDao, database.pacienteDao, database.productoDao)
    }
}
