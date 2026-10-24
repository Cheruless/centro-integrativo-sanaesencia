package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.Especialidad
import cl.sanaesencia.app.domain.model.Especialista

/**
 * Contrato para el acceso al catálogo de especialistas.
 * Permite buscar profesionales por id o por especialidad.
 */
interface EspecialistaRepository {
    /** Devuelve todos los especialistas. */
    fun getEspecialistas(): List<Especialista>
    /** Busca un especialista por su id. */
    fun getEspecialistaById(id: Int): Especialista?
    /** Filtra especialistas que ejercen la especialidad dada. */
    fun getEspecialistasByEspecialidad(especialidad: Especialidad): List<Especialista>
}
