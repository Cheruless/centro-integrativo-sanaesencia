package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.model.Especialidad
import cl.sanaesencia.app.domain.model.EstadoCita
import cl.sanaesencia.app.domain.repository.CitaRepository
import java.time.LocalDate
import java.time.LocalTime

class MockCitaRepository(
    especialistaRepo: MockEspecialistaRepository = MockEspecialistaRepository(),
    pacienteRepo: MockPacienteRepository = MockPacienteRepository()
) : CitaRepository {

    private val especialistas = especialistaRepo.getEspecialistas()
    private val pacienteActual = pacienteRepo.getPacienteActual()

    private val citas = mutableListOf(
        Cita(
            id = 1,
            fecha = LocalDate.of(2026, 10, 15),
            hora = LocalTime.of(10, 30),
            especialidad = Especialidad.AROMATERAPIA,
            precio = Especialidad.AROMATERAPIA.precioBase,
            especialista = especialistas[0],
            paciente = pacienteActual,
            estado = EstadoCita.CONFIRMADA,
            notas = "Primera consulta por insomnio y tensión laboral."
        ),
        Cita(
            id = 2,
            fecha = LocalDate.of(2026, 10, 20),
            hora = LocalTime.of(16, 0),
            especialidad = Especialidad.MASOTERAPIA,
            precio = Especialidad.MASOTERAPIA.precioBase,
            especialista = especialistas[1],
            paciente = pacienteActual,
            estado = EstadoCita.PENDIENTE,
            notas = null
        )
    )

    override fun getCitas(): List<Cita> = citas.toList()

    override fun getCitaById(id: Int): Cita? = citas.find { it.id == id }

    override fun agendarCita(cita: Cita): Int? {
        val nuevaCita = cita.copy(id = (citas.maxOfOrNull { it.id } ?: 0) + 1)
        citas.add(nuevaCita)
        return nuevaCita.id
    }

    override fun cancelarCita(id: Int): Boolean {
        val index = citas.indexOfFirst { it.id == id }
        if (index != -1) {
            citas[index] = citas[index].copy(estado = EstadoCita.CANCELADA)
            return true
        }
        return false
    }

    override fun confirmarCita(id: Int): Boolean {
        val index = citas.indexOfFirst { it.id == id }
        if (index != -1 && citas[index].estado == EstadoCita.PENDIENTE) {
            citas[index] = citas[index].copy(estado = EstadoCita.CONFIRMADA)
            return true
        }
        return false
    }

    override fun pagarCita(id: Int): Boolean {
        val index = citas.indexOfFirst { it.id == id }
        if (index != -1 && citas[index].estado == EstadoCita.PENDIENTE) {
            citas[index] = citas[index].copy(estado = EstadoCita.CONFIRMADA)
            return true
        }
        return false
    }
}
