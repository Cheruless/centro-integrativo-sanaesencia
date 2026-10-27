package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.data.local.PacienteDao
import cl.sanaesencia.app.domain.model.Paciente
import cl.sanaesencia.app.domain.repository.PacienteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/** Repositorio Room para pacientes. Consulta la tabla `pacientes`. */
class RoomPacienteRepository(private val dao: PacienteDao) : PacienteRepository {

    override fun getPacienteActual(): Paciente =
        runBlocking(Dispatchers.IO) { dao.getActual() ?: createDefault() }

    override fun getPacienteByRut(rut: String): Paciente? =
        runBlocking(Dispatchers.IO) { dao.getByRut(rut) }

    override fun updatePaciente(paciente: Paciente) {
        runBlocking(Dispatchers.IO) { dao.update(paciente) }
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
