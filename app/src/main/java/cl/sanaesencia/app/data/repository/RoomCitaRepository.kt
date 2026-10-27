package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.data.local.CitaDao
import cl.sanaesencia.app.data.local.CitaEntity
import cl.sanaesencia.app.data.local.EspecialistaDao
import cl.sanaesencia.app.data.local.PacienteDao
import cl.sanaesencia.app.data.local.ProductoDao
import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.model.EstadoCita
import cl.sanaesencia.app.domain.model.Paciente
import cl.sanaesencia.app.domain.model.Producto
import cl.sanaesencia.app.domain.repository.CitaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class RoomCitaRepository(
    private val citaDao: CitaDao,
    private val especialistaDao: EspecialistaDao,
    private val pacienteDao: PacienteDao,
    private val productoDao: ProductoDao
) : CitaRepository {

    override fun observarCitas(): Flow<List<Cita>> = combine(
        citaDao.observarTodas(),
        especialistaDao.obtenerEspecialistas(),
        pacienteDao.observarTodos()
    ) { citas: List<CitaEntity>, especialistas: List<Especialista>, pacientes: List<Paciente> ->
        val espMap = especialistas.associateBy { it.id }
        val pacMap = pacientes.associateBy { it.id }
        citas.mapNotNull { c ->
            val esp = espMap[c.especialistaId] ?: return@mapNotNull null
            val pac = pacMap[c.pacienteId] ?: return@mapNotNull null
            Cita(
                id = c.id,
                fecha = c.fecha,
                hora = c.hora,
                especialidad = c.especialidad,
                precio = c.precio,
                notas = c.notas,
                especialista = esp,
                paciente = pac,
                estado = c.estado
            )
        }
    }

    override fun observarEspecialistas(): Flow<List<Especialista>> =
        especialistaDao.obtenerEspecialistas()

    override fun observarProductos(): Flow<List<Producto>> =
        productoDao.observarTodos()

    override fun observarPaciente(): Flow<Paciente?> =
        pacienteDao.observarTodos().map { it.firstOrNull() }

    override suspend fun getPacienteActual(): Paciente =
        pacienteDao.getActual() ?: error("No hay paciente cargado")

    override suspend fun actualizarPaciente(paciente: Paciente) {
        pacienteDao.insert(paciente)
    }

    override suspend fun agendarCita(cita: Cita): Boolean = try {
        citaDao.insert(
            CitaEntity(
                fecha = cita.fecha,
                hora = cita.hora,
                especialidad = cita.especialidad,
                precio = cita.precio,
                notas = cita.notas,
                especialistaId = cita.especialista.id,
                pacienteId = cita.paciente.id,
                estado = cita.estado
            )
        )
        true
    } catch (e: Exception) {
        false
    }

    override suspend fun agendarCitaYObtenerId(cita: Cita): Int? = try {
        val id = citaDao.insert(
            CitaEntity(
                fecha = cita.fecha,
                hora = cita.hora,
                especialidad = cita.especialidad,
                precio = cita.precio,
                notas = cita.notas,
                especialistaId = cita.especialista.id,
                pacienteId = cita.paciente.id,
                estado = cita.estado
            )
        )
        id.toInt()
    } catch (e: Exception) {
        null
    }

    override suspend fun cancelarCita(id: Int): Boolean =
        citaDao.cancelar(id) > 0
}
