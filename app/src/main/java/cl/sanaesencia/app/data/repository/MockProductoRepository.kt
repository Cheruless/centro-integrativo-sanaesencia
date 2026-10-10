package cl.sanaesencia.app.data.repository

import cl.sanaesencia.app.domain.model.Producto
import cl.sanaesencia.app.domain.repository.ProductoRepository

class MockProductoRepository : ProductoRepository {

    private val productos = mutableListOf(
        Producto(
            id = 1,
            nombre = "Aceite Esencial de Lavanda 15ml",
            descripcion = "Aceite 100% puro para relajación, aromaterapia y reducción del estrés nocturno.",
            precio = 12990.0,
            categoriaEspecialidad = "Aromaterapia",
            stock = 15
        ),
        Producto(
            id = 2,
            nombre = "Roll-on Anti-Estrés Menta y Eucalipto",
            descripcion = "Formulación natural para aplicar en sienes y muñecas ante momentos de tensión.",
            precio = 7990.0,
            categoriaEspecialidad = "Aromaterapia",
            stock = 20
        ),
        Producto(
            id = 3,
            nombre = "Crema Corporal Masaje Árnica y Caléndula",
            descripcion = "Crema analgésica e inflamatoria recomendada para alivio muscular posterior a masajes.",
            precio = 14500.0,
            categoriaEspecialidad = "Masoterapia",
            stock = 8
        ),
        Producto(
            id = 4,
            nombre = "Frasco Personalizado Flores de Bach 30ml",
            descripcion = "Preparado floral enfocado en calma mental, concentración y equilibrio emocional.",
            precio = 10990.0,
            categoriaEspecialidad = "Terapias Florales",
            stock = 25
        ),
        Producto(
            id = 5,
            nombre = "Banda Elástica Terapéutica Kinesiológica",
            descripcion = "Banda de resistencia media para ejercicios kinesiológicos de fortalecimiento muscular.",
            precio = 8990.0,
            categoriaEspecialidad = "Kinesiología Holística",
            stock = 12
        )
    )

    override fun getProductos(): List<Producto> = productos.toList()

    override fun getProductoById(id: Int): Producto? =
        productos.find { it.id == id }

    override fun getProductosByCategoria(categoria: String): List<Producto> =
        productos.filter { it.categoriaEspecialidad.contains(categoria, ignoreCase = true) }
}
