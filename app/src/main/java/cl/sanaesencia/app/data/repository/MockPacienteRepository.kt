package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.domain.model.Paciente
import cl.sanaesencia.app.domain.repository.PacienteRepository

class MockPacienteRepository : PacienteRepository {

    private var pacienteActual = Paciente(
        id = 101,
        rut = "20.781.681-7",
        primerNombre = "Exequiel",
        segundoNombre = "Raúl",
        apellidoPaterno = "Pulgar",
        apellidoMaterno = "Cerda",
        email = "exequiel.pulgar@gmail.com",
        telefono = "+56 9 9876 1234",
        fechaNacimiento = "2001-05-25"
    )

    override fun getPacienteActual(): Paciente = pacienteActual

    override fun getPacienteByRut(rut: String): Paciente? {
        return if (pacienteActual.rut == rut) pacienteActual else null
    }

    override fun updatePaciente(paciente: Paciente) {
        pacienteActual = paciente
    }
}
