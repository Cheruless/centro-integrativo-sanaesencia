package cl.sanaesencia.app.domain.model

/**
 * Representa un producto o insumo comercializado en la clínica Sanaesencia.
 * Los productos se asocian a categorías o especialidades temáticas
 * (ej: Aromaterapia, Masoterapia, Terapia Floral, Cuidado Personal).
 *
 * TODO (Módulo Productos):
 *  1. Evaluar si un producto requiere múltiples etiquetas/tags (List<String>) si está
 *     asociado a más de una especialidad temática.
 *  2. Considerar la creación de una data class o Enum 'CategoriaProducto' para
 *     facilitar el filtrado en la interfaz de usuario.
 */
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoriaEspecialidad: String, // ej: "Aromaterapia", "Masajes", "Florales"
    val stock: Int = 10,
    val imagenResId: Int? = null
)