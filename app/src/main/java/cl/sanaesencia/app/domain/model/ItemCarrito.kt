package cl.sanaesencia.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Item dentro del carrito de compras. Cada fila representa
 * un producto con su cantidad. Un carrito tiene múltiples items.
 */
@Entity(tableName = "carrito")
data class ItemCarrito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,
    val nombreProducto: String,
    val precio: Double,
    val cantidad: Int,
    val imagenResId: Int? = null
)
