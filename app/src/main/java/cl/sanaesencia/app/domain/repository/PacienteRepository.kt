package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.Paciente

interface PacienteRepository {
    fun getPacienteActual(): Paciente
    fun getPacienteByRut(rut: String): Paciente?
    fun updatePaciente(paciente: Paciente)
    suspend fun setPacienteActual(id: Int)
}
