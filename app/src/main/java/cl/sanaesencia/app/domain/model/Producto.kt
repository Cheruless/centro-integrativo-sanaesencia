package cl.sanaesencia.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Producto o insumo comercializado en la clínica.
 * Se asocia a una categoría temática (ej: Aromaterapia, Masajes).
 */
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoriaEspecialidad: String,
    val stock: Int = 10,
    val imagenResId: Int? = null
)
