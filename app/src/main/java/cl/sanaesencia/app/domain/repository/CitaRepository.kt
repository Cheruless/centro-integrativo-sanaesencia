package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.Cita

interface CitaRepository {
    fun getCitas(): List<Cita>
    fun getCitaById(id: Int): Cita?
    /** Crea la cita y devuelve el id asignado, o null si falló. */
    fun agendarCita(cita: Cita): Int?
    fun cancelarCita(id: Int): Boolean
    /** PENDIENTE → CONFIRMADA (flujo sin pago / administrativo). */
    fun confirmarCita(id: Int): Boolean
    /** PENDIENTE → CONFIRMADA (cierre del mock de pago). */
    fun pagarCita(id: Int): Boolean
}
