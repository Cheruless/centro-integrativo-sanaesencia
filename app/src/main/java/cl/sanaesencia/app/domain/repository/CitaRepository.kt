package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.Cita

interface CitaRepository {
    fun getCitas(): List<Cita>
    fun getCitaById(id: Int): Cita?
    fun agendarCita(cita: Cita): Boolean
    fun cancelarCita(id: Int): Boolean
}
