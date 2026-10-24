package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.domain.model.Especialidad
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.repository.EspecialistaRepository
import java.time.DayOfWeek

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
            especialidades = listOf(Especialidad.AROMATERAPIA, Especialidad.TERAPIA_FLORAL),
            diasAtencion = setOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
            ),
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
            especialidades = listOf(Especialidad.QUIROPRAXIA, Especialidad.MASOTERAPIA),
            diasAtencion = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY),
            descripcion = "Quiropráctico y masoterapeuta enfocado en alivio del dolor musculoesquelético.",
            email = "mateo.rojas@sanaesencia.cl",
            telefono = "+56 9 8765 4321"
        ),
        Especialista(
            id = 3,
            primerNombre = "Valentina",
            segundoNombre = "Paz",
            apellidoPaterno = "Fuentes",
            apellidoMaterno = "Vargas",
            especialidades = listOf(Especialidad.PSICOLOGIA, Especialidad.TERAPIA_FLORAL),
            diasAtencion = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            descripcion = "Psicóloga y terapeuta floral especializada en Flores de Bach para armonización emocional.",
            email = "valentina.fuentes@sanaesencia.cl",
            telefono = "+56 9 5555 1234"
        ),
        Especialista(
            id = 4,
            primerNombre = "Gonzalo",
            segundoNombre = "Andrés",
            apellidoPaterno = "Araya",
            apellidoMaterno = "López",
            especialidades = listOf(
                Especialidad.PSICOPEDAGOGIA,
                Especialidad.EVALUACIONES_NEURODESARROLLO
            ),
            diasAtencion = setOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY
            ),
            descripcion = "Psicopedagogo especializado en evaluaciones del neurodesarrollo y apoyo al aprendizaje.",
            email = "gonzalo.araya@sanaesencia.cl",
            telefono = "+56 9 4444 8888"
        )
    )

    override fun getEspecialistas(): List<Especialista> = especialistas.toList()

    override fun getEspecialistaById(id: Int): Especialista? =
        especialistas.find { it.id == id }

    override fun getEspecialistasByEspecialidad(especialidad: Especialidad): List<Especialista> =
        especialistas.filter { especialidad in it.especialidades }
}
