package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.repository.EspecialistaRepository

/**
 * Implementación de prueba (Mock / En Memoria) para el catálogo de Especialistas.
 * Utilizada durante la fase de desarrollo del MVP.
 */
class MockEspecialistaRepository : EspecialistaRepository {

    private val especialistas = mutableListOf(
        Especialista(
            id = 1,
            primerNombre = "Camila",
            segundoNombre = "Andrea",
            apellidoPaterno = "Silva",
            apellidoMaterno = "Morales",
            especialidad = "Aromaterapia",
            descripcion = "Especialista en óleos esenciales y tratamientos holísticos para el estrés y la ansiedad.",
            email = "camila.silva@sanaesencia.cl",
            telefono = "+56 9 1234 5678"
        ),
        Especialista(
            id = 2,
            primerNombre = "Mateo",
            segundoNombre = "Ignacio",
            apellidoPaterno = "Rojas",
            apellidoMaterno = "Castro",
            especialidad = "Quiropraxia",
            descripcion = "Quiropráctico.",
            email = "mateo.rojas@sanaesencia.cl",
            telefono = "+56 9 8765 4321"
        ),
        Especialista(
            id = 3,
            primerNombre = "Valentina",
            segundoNombre = "Paz",
            apellidoPaterno = "Fuentes",
            apellidoMaterno = "Vargas",
            especialidad = "Terapias Florales",
            descripcion = "Terapeuta floral especializada en Flores de Bach para armonización emocional.",
            email = "valentina.fuentes@sanaesencia.cl",
            telefono = "+56 9 5555 1234"
        ),
        Especialista(
            id = 4,
            primerNombre = "Gonzalo",
            segundoNombre = "Andrés",
            apellidoPaterno = "Araya",
            apellidoMaterno = "López",
            especialidad = "Kinesiología Holística",
            descripcion = "Kinesiólogo enfocado en rehabilitación neuromuscular e integración cuerpo-mente.",
            email = "gonzalo.araya@sanaesencia.cl",
            telefono = "+56 9 4444 8888"
        )
    )

    override fun getEspecialistas(): List<Especialista> = especialistas.toList()

    override fun getEspecialistaById(id: Int): Especialista? =
        especialistas.find { it.id == id }

    override fun getEspecialistasByEspecialidad(especialidad: String): List<Especialista> =
        especialistas.filter { it.especialidad.contains(especialidad, ignoreCase = true) }
}
