package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.Cita

/**
 * Contrato para el acceso a citas médicas.
 * Define las operaciones CRUD y los cambios de estado.
 */
interface CitaRepository {
    /** Devuelve todas las citas del paciente actual. */
    fun getCitas(): List<Cita>
    /** Busca una cita por su id. */
    fun getCitaById(id: Int): Cita?
    /** Crea una cita y devuelve el id asignado, o null si falló. */
    fun agendarCita(cita: Cita): Int?
    /** Cambia el estado de la cita a CANCELADA. */
    fun cancelarCita(id: Int): Boolean
    /** Cambia la cita de PENDIENTE a CONFIRMADA (sin pago). */
    fun confirmarCita(id: Int): Boolean
    /** Cambia la cita de PENDIENTE a CONFIRMADA (pago mock). */
    fun pagarCita(id: Int): Boolean
}
