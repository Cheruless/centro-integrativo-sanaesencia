package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.data.local.CitaDao
import cl.sanaesencia.app.data.local.CitaEntity
import cl.sanaesencia.app.data.local.EspecialistaDao
import cl.sanaesencia.app.data.local.PacienteDao
import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.repository.CitaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * Repositorio Room para citas. Reconstruye el objeto Cita completo
 * a partir de CitaEntity (que guarda IDs de especialista y paciente).
 */
class RoomCitaRepository(
    private val citaDao: CitaDao,
    private val especialistaDao: EspecialistaDao,
    private val pacienteDao: PacienteDao
) : CitaRepository {

    override fun getCitas(): List<Cita> =
        runBlocking(Dispatchers.IO) {
            citaDao.getAll().mapNotNull { it.toDomain() }
        }

    override fun getCitaById(id: Int): Cita? =
        runBlocking(Dispatchers.IO) {
            citaDao.getById(id)?.toDomain()
        }

    override fun agendarCita(cita: Cita): Int? =
        runBlocking(Dispatchers.IO) {
            val entity = cita.toEntity()
            val id = citaDao.insert(entity)
            if (id > 0) id.toInt() else null
        }

    override fun cancelarCita(id: Int): Boolean =
        runBlocking(Dispatchers.IO) { citaDao.cancelar(id) > 0 }

    override fun confirmarCita(id: Int): Boolean =
        runBlocking(Dispatchers.IO) { citaDao.confirmar(id) > 0 }

    override fun pagarCita(id: Int): Boolean =
        runBlocking(Dispatchers.IO) { citaDao.pagar(id) > 0 }

    // --- Mapeo Entity ↔ Dominio ---

    private suspend fun CitaEntity.toDomain(): Cita? {
        val especialista = especialistaDao.obtenerEspecialistaPorId(especialistaId) ?: return null
        val paciente = pacienteDao.getActual() ?: return null
        return Cita(
            id = id,
            fecha = fecha,
            hora = hora,
            especialidad = especialidad,
            precio = precio,
            notas = notas,
            especialista = especialista,
            paciente = paciente,
            estado = estado
        )
    }

    private fun Cita.toEntity(): CitaEntity =
        CitaEntity(
            id = if (id == 0) 0 else id,
            fecha = fecha,
            hora = hora,
            especialidad = especialidad,
            precio = precio,
            notas = notas,
            especialistaId = especialista.id,
            pacienteId = paciente.id,
            estado = estado
        )
}
