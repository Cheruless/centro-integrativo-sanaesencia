package cl.sanaesencia.app.data

import android.content.Context
import androidx.room.Room
import cl.sanaesencia.app.data.local.SanaesenciaDatabase
import cl.sanaesencia.app.data.local.SeedCallback
import cl.sanaesencia.app.data.repository.RoomCitaRepository
import cl.sanaesencia.app.data.repository.RoomEspecialistaRepository
import cl.sanaesencia.app.data.repository.RoomPacienteRepository
import cl.sanaesencia.app.data.repository.RoomProductoRepository
import cl.sanaesencia.app.data.repository.RoomCarritoRepository
import cl.sanaesencia.app.domain.repository.CitaRepository
import cl.sanaesencia.app.domain.repository.EspecialistaRepository
import cl.sanaesencia.app.domain.repository.PacienteRepository
import cl.sanaesencia.app.domain.repository.ProductoRepository
import cl.sanaesencia.app.domain.repository.CarritoRepository

object ServiceLocator {

    private lateinit var database: SanaesenciaDatabase

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
        RoomPacienteRepository(database.pacienteDao, database.sesionDao)
    }

    val especialistaRepository: EspecialistaRepository by lazy {
        RoomEspecialistaRepository(database.especialistaDao)
    }

    val citaRepository: CitaRepository by lazy {
        RoomCitaRepository(database.citaDao, database.especialistaDao, database.pacienteDao)
    }

    val carritoRepository: CarritoRepository by lazy {
        RoomCarritoRepository(database.carritoDao, database.productoDao)
    }
}
