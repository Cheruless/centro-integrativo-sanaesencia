package cl.sanaesencia.app.domain.model

/**
 * Producto o insumo comercializado en la clínica.
 * Se asocia a una categoría temática (ej: Aromaterapia, Masajes).
 */
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoriaEspecialidad: String,
    val stock: Int = 10,
    val imagenResId: Int? = null
)
