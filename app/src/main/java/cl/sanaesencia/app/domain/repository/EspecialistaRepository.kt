package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.Especialista

interface EspecialistaRepository {
    fun getEspecialistas(): List<Especialista>
    fun getEspecialistaById(id: Int): Especialista?
    fun getEspecialistasByEspecialidad(especialidad: String): List<Especialista>
}
