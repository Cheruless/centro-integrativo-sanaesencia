package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.data.local.PacienteDao
import cl.sanaesencia.app.data.local.SesionDao
import cl.sanaesencia.app.data.local.SesionEntity
import cl.sanaesencia.app.domain.model.Paciente
import cl.sanaesencia.app.domain.repository.PacienteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class RoomPacienteRepository(
    private val dao: PacienteDao,
    private val sesionDao: SesionDao
) : PacienteRepository {

    override fun getPacienteActual(): Paciente =
        runBlocking(Dispatchers.IO) {
            val activeId = sesionDao.getActivePacienteId()
            val paciente = if (activeId != null) dao.getById(activeId) else null
            paciente ?: dao.getActual() ?: createDefault()
        }

    override fun getPacienteByRut(rut: String): Paciente? =
        runBlocking(Dispatchers.IO) { dao.getByRut(rut) }

    override fun updatePaciente(paciente: Paciente) {
        runBlocking(Dispatchers.IO) { dao.update(paciente) }
    }

    override suspend fun setPacienteActual(id: Int) {
        sesionDao.setSession(SesionEntity(id = 1, pacienteId = id))
    }

    private fun createDefault(): Paciente =
        Paciente(
            id = 0,
            rut = "20.781.681-7",
            primerNombre = "Exequiel",
            segundoNombre = "Raúl",
            apellidoPaterno = "Pulgar",
            apellidoMaterno = "Cerda",
            email = "exequiel.pulgar@gmail.com",
            telefono = "+56 9 9876 1234",
            fechaNacimiento = "2001-05-25"
        )
}
