package cl.sanaesencia.app.domain.model

/*
* TODO:
*   Desarrollar una data class de especialidades, ya que en relaciones, un Especialista tiene una
*       o varias especialidades
*   Especialides impartidas por el centro integrativo:
*       - Psicología
*       - Terapia ocupacional
*       - Fonoaudiología
*       - Psicopedagogía
*       - Nutrición
*       - Matrona
*       - Quiropraxia
*       - Masajes
*       - Evaluaciones del neurodesarrollo
*       - Terapia flotar
*       - Aromaterapia
* */

data class Especialista(
    val id: Int,
    val primerNombre: String,
    val segundoNombre: String? = null,
    val apellidoPaterno: String,
    val apellidoMaterno: String,

    val especialidad: String,
    val descripcion: String = "",
    val email: String = "",
    val telefono: String = "",
    val imagenResId: Int? = null
) {
    val nombreCompleto: String
        get() = listOfNotNull(primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    val nombreCorto: String
        get() = "$primerNombre $apellidoPaterno $apellidoMaterno"
}