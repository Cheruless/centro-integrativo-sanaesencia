package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.data.local.ProductoDao
import cl.sanaesencia.app.domain.model.Producto
import cl.sanaesencia.app.domain.repository.ProductoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/** Repositorio Room para productos. Consulta la tabla `productos`. */
class RoomProductoRepository(private val dao: ProductoDao) : ProductoRepository {

    override fun getProductos(): List<Producto> =
        runBlocking(Dispatchers.IO) { dao.getAll() }

    override fun getProductoById(id: Int): Producto? =
        runBlocking(Dispatchers.IO) { dao.getById(id) }

    override fun getProductosByCategoria(categoria: String): List<Producto> =
        runBlocking(Dispatchers.IO) { dao.getByCategoria(categoria) }
}
