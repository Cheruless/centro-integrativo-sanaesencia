package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.model.EstadoCita
import cl.sanaesencia.app.domain.repository.CitaRepository

class MockCitaRepository(
    especialistaRepo: MockEspecialistaRepository = MockEspecialistaRepository(),
    pacienteRepo: MockPacienteRepository = MockPacienteRepository()
) : CitaRepository {

    private val especialistas = especialistaRepo.getEspecialistas()
    private val pacienteActual = pacienteRepo.getPacienteActual()

    private val citas = mutableListOf(
        Cita(
            id = 1,
            fecha = "2026-10-15",
            hora = "10:30",
            servicio = "Sesión de Aromaterapia Relajante",
            especialista = especialistas[0],
            paciente = pacienteActual,
            estado = EstadoCita.CONFIRMADA,
            precio = 70000.0,
            notas = "Primera consulta por insomnio y tensión laboral."
        ),
        Cita(
            id = 2,
            fecha = "2026-10-20",
            hora = "16:00",
            servicio = "Masaje Descontracturante Espalda",
            especialista = especialistas[1],
            paciente = pacienteActual,
            estado = EstadoCita.PENDIENTE,
            precio = 40000.0,
            notas = null
        )
    )

    override fun getCitas(): List<Cita> = citas.toList()

    override fun getCitaById(id: Int): Cita? = citas.find { it.id == id }

    override fun agendarCita(cita: Cita): Boolean {
        val nuevaCita = cita.copy(id = (citas.maxOfOrNull { it.id } ?: 0) + 1)
        citas.add(nuevaCita)
        return true
    }

    override fun cancelarCita(id: Int): Boolean {
        val index = citas.indexOfFirst { it.id == id }
        if (index != -1) {
            citas[index] = citas[index].copy(estado = EstadoCita.CANCELADA)
            return true
        }
        return false
    }
}
