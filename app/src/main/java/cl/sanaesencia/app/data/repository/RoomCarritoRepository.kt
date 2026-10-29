package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.data.local.CarritoDao
import cl.sanaesencia.app.data.local.ProductoDao
import cl.sanaesencia.app.domain.model.ItemCarrito
import cl.sanaesencia.app.domain.repository.CarritoRepository
import kotlinx.coroutines.flow.Flow

class RoomCarritoRepository(
    private val carritoDao: CarritoDao,
    private val productoDao: ProductoDao
) : CarritoRepository {

    override fun obtenerCarritoFlow(): Flow<List<ItemCarrito>> =
        carritoDao.obtenerCarritoFlow()

    override suspend fun obtenerCarrito(): List<ItemCarrito> =
        carritoDao.obtenerCarrito()

    override suspend fun obtenerItemPorProductoId(productoId: Int): ItemCarrito? =
        carritoDao.obtenerItemPorProductoId(productoId)

    override suspend fun agregarOIncrementar(productoId: Int, nombre: String, precio: Double, imagenResId: Int?, cantidad: Int) {
        val existente = carritoDao.obtenerItemPorProductoId(productoId)
        if (existente != null) {
            carritoDao.actualizarItem(existente.copy(cantidad = existente.cantidad + cantidad))
        } else {
            carritoDao.insertarItem(
                ItemCarrito(
                    productoId = productoId,
                    nombreProducto = nombre,
                    precio = precio,
                    cantidad = cantidad,
                    imagenResId = imagenResId
                )
            )
        }
    }

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

    override suspend fun comprarProductos(): Boolean {
        val items = carritoDao.obtenerCarrito()
        // Validar stock antes de descontar (transacción: todo o nada)
        for (item in items) {
            val producto = productoDao.getById(item.productoId) ?: return false
            if (item.cantidad > producto.stock) return false
        }
        // Descontar stock
        for (item in items) {
            val resultado = productoDao.descontarStock(item.productoId, item.cantidad)
            if (resultado == 0) return false // no alcanzó stock
        }
        carritoDao.vaciarCarrito()
        return true
    }
}
