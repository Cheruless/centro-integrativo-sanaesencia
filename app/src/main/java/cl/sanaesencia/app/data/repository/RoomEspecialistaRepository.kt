package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.data.local.EspecialistaDao
import cl.sanaesencia.app.domain.model.Especialidad
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.repository.EspecialistaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/** Repositorio Room para especialistas. Consulta la tabla `especialistas`. */
class RoomEspecialistaRepository(private val dao: EspecialistaDao) : EspecialistaRepository {

    override fun getEspecialistas(): List<Especialista> =
        runBlocking(Dispatchers.IO) { dao.obtenerEspecialistas().first() }

    override fun getEspecialistaById(id: Int): Especialista? =
        runBlocking(Dispatchers.IO) { dao.obtenerEspecialistaPorId(id) }

    override fun getEspecialistasByEspecialidad(especialidad: Especialidad): List<Especialista> =
        runBlocking(Dispatchers.IO) {
            dao.obtenerEspecialistas().first().filter { especialidad in it.especialidades }
        }
}
