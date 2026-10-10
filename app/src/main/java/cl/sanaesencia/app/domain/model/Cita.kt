package cl.sanaesencia.app.domain.model

enum class EstadoCita {
    PENDIENTE,
    CONFIRMADA,
    REALIZADA,
    CANCELADA
}

data class Cita(
    val id: Int,
    val fecha: String, // Formato "YYYY-MM-DD"
    val hora: String,  // Formato "HH:mm", ej: "15:30"
    val servicio: String, // ej: "Masaje Descontracturante", "Sesión de Aromaterapia"
    val precio: Double = 0.0,
    val notas: String? = null,

    val especialista: Especialista,
    val paciente: Paciente,
    val estado: EstadoCita = EstadoCita.PENDIENTE
)