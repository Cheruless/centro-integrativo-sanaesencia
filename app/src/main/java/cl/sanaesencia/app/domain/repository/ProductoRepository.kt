package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.Producto

interface ProductoRepository {
    fun getProductos(): List<Producto>
    fun getProductoById(id: Int): Producto?
    fun getProductosByCategoria(categoria: String): List<Producto>
}
