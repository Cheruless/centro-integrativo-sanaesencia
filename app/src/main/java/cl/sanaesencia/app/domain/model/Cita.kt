package cl.sanaesencia.app.domain.model

class Cita(
    val id: Int,
    val fecha: String, // Considerar usar Date
    val especialista: Especialista,
    val paciente: Paciente
) {
}