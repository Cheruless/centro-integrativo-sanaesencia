package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.ItemCarrito
import kotlinx.coroutines.flow.Flow

interface CarritoRepository {
    fun obtenerCarritoFlow(): Flow<List<ItemCarrito>>
    suspend fun obtenerCarrito(): List<ItemCarrito>
    suspend fun obtenerItemPorProductoId(productoId: Int): ItemCarrito?
    suspend fun insertarItem(item: ItemCarrito)
    suspend fun actualizarItem(item: ItemCarrito)
    suspend fun eliminarItem(item: ItemCarrito)
    suspend fun eliminarPorId(id: Int)
    suspend fun vaciarCarrito()
}
