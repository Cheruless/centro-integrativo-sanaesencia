package cl.sanaesencia.app.domain.repository

import cl.sanaesencia.app.domain.model.Producto

/**
 * Contrato para el acceso al catálogo de productos.
 * Permite buscar productos por id o por categoría.
 */
interface ProductoRepository {
    /** Devuelve todos los productos. */
    fun getProductos(): List<Producto>
    /** Busca un producto por su id. */
    fun getProductoById(id: Int): Producto?
    /** Filtra productos por categoría temática. */
    fun getProductosByCategoria(categoria: String): List<Producto>
}
