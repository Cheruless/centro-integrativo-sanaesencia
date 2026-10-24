package cl.sanaesencia.app.domain.model

/**
 * Catálogo de especialidades del centro. Cada una define su precio,
 * cuántos bloques de 30 min dura la sesión y una descripción corta.
 */
enum class Especialidad(
    val displayName: String,
    val precioBase: Double,
    val bloquesPorSesion: Int,
    val descripcion: String
) {
    PSICOLOGIA(
        "Psicología", 45_000.0, 2,
        "Acompañamiento emocional y herramientas para el bienestar mental. Sesiones de 60 minutos orientadas a ansiedad, estrés y procesos personales."
    ),
    TERAPIA_OCUPACIONAL(
        "Terapia Ocupacional", 40_000.0, 1,
        "Estimula la autonomía y las habilidades necesarias para la vida diaria. Ideal para niños y adultos en procesos de rehabilitación."
    ),
    FONOAUDIOLOGIA(
        "Fonoaudiología", 38_000.0, 1,
        "Evaluación y tratamiento de la comunicación, lenguaje y deglución. Atención personalizada según cada etapa del desarrollo."
    ),
    PSICOPEDAGOGIA(
        "Psicopedagogía", 35_000.0, 1,
        "Apoyo en dificultades de aprendizaje y estrategias de estudio. Orientación para estudiantes, familias y docentes."
    ),
    NUTRICION(
        "Nutrición", 30_000.0, 1,
        "Planes alimentarios personalizados y educación nutricional. Enfoque en salud, rendimiento y relación sana con la comida."
    ),
    MATRONA(
        "Matrona", 35_000.0, 1,
        "Salud integral de la mujer: control, planificación y acompañamiento. Espacio confiable para cada etapa de la vida."
    ),
    QUIROPRAXIA(
        "Quiropraxia", 50_000.0, 1,
        "Alineación de la columna y alivio del dolor musculoesquelético. Técnicas suaves para postura, movilidad y bienestar físico."
    ),
    MASOTERAPIA(
        "Masoterapia / Masajes", 35_000.0, 2,
        "Masajes terapéuticos para relajar músculos y liberar tensión. Sesiones de 60 minutos adaptadas a tus necesidades."
    ),
    EVALUACIONES_NEURODESARROLLO(
        "Evaluaciones del Neurodesarrollo", 70_000.0, 2,
        "Evaluación integral del neurodesarrollo en niños. Identifica fortalezas y áreas de apoyo con informe profesional."
    ),
    TERAPIA_FLORAL(
        "Terapia Floral", 30_000.0, 1,
        "Esencias florales para equilibrar emociones y estados de ánimo. Enfoque suave y natural del bienestar integral."
    ),
    AROMATERAPIA(
        "Aromaterapia", 30_000.0, 1,
        "Uso terapéutico de aceites esenciales para relajar cuerpo y mente. Ideal para estrés, insomnio y tensión acumulada."
    );

    /** Duración total de la sesión en minutos (30 o 60). */
    val duracionMinutos: Int
        get() = bloquesPorSesion * Horario.BLOQUE_MINUTOS.toInt()
}
