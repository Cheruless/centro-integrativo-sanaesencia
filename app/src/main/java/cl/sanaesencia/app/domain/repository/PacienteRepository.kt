package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.Paciente

/**
 * Contrato para la gestión del perfil del paciente.
 * El MVP maneja un único paciente logueado.
 */
interface PacienteRepository {
    /** Devuelve el paciente activo de la sesión. */
    fun getPacienteActual(): Paciente
    /** Busca un paciente por su RUT. */
    fun getPacienteByRut(rut: String): Paciente?
    /** Actualiza los datos del paciente. */
    fun updatePaciente(paciente: Paciente)
}
