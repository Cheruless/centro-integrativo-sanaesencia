package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.model.Paciente
import cl.sanaesencia.app.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface CitaRepository {
    fun observarCitas(): Flow<List<Cita>>
    fun observarEspecialistas(): Flow<List<Especialista>>
    fun observarProductos(): Flow<List<Producto>>
    fun observarPaciente(): Flow<Paciente?>
    suspend fun getPacienteActual(): Paciente
    suspend fun actualizarPaciente(paciente: Paciente)
    suspend fun agendarCita(cita: Cita): Boolean
    suspend fun agendarCitaYObtenerId(cita: Cita): Int?
    suspend fun cancelarCita(id: Int): Boolean
}
