package cl.sanaesencia.app.domain.model

//TODO:
//  ECHAR UN VISTAZO AL SITIO WEB OFICIAL DE sanaesencia Y AVERIGUAR SUS PRODUCTOS.
//  CONSIDERAR CREAR UNA CLASE PARA CATEGORIZAR ESTOS PRODUCTOS O SI SOLO DEBE SER UN STRING Y YA.

/*
 *  Pensaba que tal vez se pueda considerar productos relacionados a una especialidad ""temática"".
 *  Ejemplo, productos que tengan como un tag para aroma terapia.
 *  Productos relacionados a masajes.
 *  Un producto puede estar relacionado a varias especialidades, puede darse el caso
*/

/**
 * Representa un producto o insumo comercializado en la clínica Sanaesencia.
 * Los productos se asocian a categorías o especialidades temáticas
 * (ej: Aromaterapia, Masoterapia, Terapia Floral, Cuidado Personal).
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