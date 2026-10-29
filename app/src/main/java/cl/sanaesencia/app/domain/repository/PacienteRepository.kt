package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.Paciente

/**
 * Contrato para la gestión del perfil del paciente.
 * El MVP maneja un único paciente activo por sesión.
 */
interface PacienteRepository {
    /** Devuelve el paciente activo de la sesión. */
    fun getPacienteActual(): Paciente
    /** Busca un paciente por su run (sin puntos). */
    fun getPacienteByRun(run: String): Paciente?
    /** Actualiza o inserta los datos del paciente. */
    fun updatePaciente(paciente: Paciente)
    /** Establece el paciente activo por su id. */
    suspend fun setPacienteActual(id: Int)
}
