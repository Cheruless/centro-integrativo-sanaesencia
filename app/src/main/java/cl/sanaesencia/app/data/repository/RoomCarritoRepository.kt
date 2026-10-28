package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.data.local.CarritoDao
import cl.sanaesencia.app.domain.model.ItemCarrito
import cl.sanaesencia.app.domain.repository.CarritoRepository
import kotlinx.coroutines.flow.Flow

class RoomCarritoRepository(private val carritoDao: CarritoDao) : CarritoRepository {

    override fun obtenerCarritoFlow(): Flow<List<ItemCarrito>> =
        carritoDao.obtenerCarritoFlow()

    override suspend fun obtenerCarrito(): List<ItemCarrito> =
        carritoDao.obtenerCarrito()

    override suspend fun obtenerItemPorProductoId(productoId: Int): ItemCarrito? =
        carritoDao.obtenerItemPorProductoId(productoId)

    override suspend fun insertarItem(item: ItemCarrito) {
        carritoDao.insertarItem(item)
    }

    override suspend fun actualizarItem(item: ItemCarrito) {
        carritoDao.actualizarItem(item)
    }

    override suspend fun eliminarItem(item: ItemCarrito) {
        carritoDao.eliminarItem(item)
    }

    override suspend fun eliminarPorId(id: Int) {
        carritoDao.eliminarPorId(id)
    }

    override suspend fun vaciarCarrito() {
        carritoDao.vaciarCarrito()
    }
}
