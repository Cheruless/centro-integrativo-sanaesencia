package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.ItemCarrito
import kotlinx.coroutines.flow.Flow

/**
 * Contrato para el carrito de compras. Permite agregar,
 * modificar y eliminar items. El carrito es un solo conjunto
 * de productos con cantidades.
 */
interface CarritoRepository {
    /** Observa el carrito en tiempo real. */
    fun obtenerCarritoFlow(): Flow<List<ItemCarrito>>
    /** Devuelve los items actuales del carrito. */
    suspend fun obtenerCarrito(): List<ItemCarrito>
    /** Busca un item por el id del producto. */
    suspend fun obtenerItemPorProductoId(productoId: Int): ItemCarrito?
    /** Agrega un producto al carrito con la cantidad indicada. Si ya existe, suma esa cantidad. */
    suspend fun agregarOIncrementar(productoId: Int, nombre: String, precio: Double, imagenResId: Int?, cantidad: Int = 1)
    /** Inserta o reemplaza un item directo. */
    suspend fun insertarItem(item: ItemCarrito)
    /** Actualiza un item existente. */
    suspend fun actualizarItem(item: ItemCarrito)
    /** Elimina un item del carrito. */
    suspend fun eliminarItem(item: ItemCarrito)
    /** Elimina un item por su id. */
    suspend fun eliminarPorId(id: Int)
    /** Vacía todo el carrito. */
    suspend fun vaciarCarrito()

    /**
     * Compra los productos del carrito: descuenta stock y vacía el carrito.
     * Valida que haya stock suficiente para cada item.
     * @return true si la compra fue exitosa, false si no hay stock suficiente.
     */
    suspend fun comprarProductos(): Boolean
}
