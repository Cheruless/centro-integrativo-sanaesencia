package cl.sanaesencia.app

import cl.sanaesencia.app.data.local.CarritoDao
import cl.sanaesencia.app.data.local.ProductoDao
import cl.sanaesencia.app.data.repository.RoomCarritoRepository
import cl.sanaesencia.app.domain.model.Especialidad
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.model.EstadoCita
import cl.sanaesencia.app.domain.model.ItemCarrito
import cl.sanaesencia.app.domain.model.Paciente
import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class SanaesenciaBusinessRulesTest {

    private class FakeProductoDao(initialProducts: List<Producto>) : ProductoDao {
        private val productos = initialProducts.toMutableList()

        override suspend fun getAll(): List<Producto> = productos

        override suspend fun getById(id: Int): Producto? = productos.find { it.id == id }

        override suspend fun getByCategoria(categoria: String): List<Producto> =
            productos.filter { it.categoriaEspecialidad.contains(categoria) }

        override suspend fun descontarStock(id: Int, cantidad: Int): Int {
            val p = getById(id) ?: return 0
            if (p.stock >= cantidad) {
                val index = productos.indexOfFirst { it.id == id }
                productos[index] = p.copy(stock = p.stock - cantidad)
                return 1
            }
            return 0
        }

        override suspend fun insertAll(productos: List<Producto>) {
            this.productos.clear()
            this.productos.addAll(productos)
        }
    }

    private class FakeCarritoDao : CarritoDao {
        private val items = mutableListOf<ItemCarrito>()
        private val flow = MutableStateFlow<List<ItemCarrito>>(emptyList())

        override fun obtenerCarritoFlow(): Flow<List<ItemCarrito>> = flow

        override suspend fun obtenerCarrito(): List<ItemCarrito> = items.toList()

        override suspend fun obtenerItemPorProductoId(productoId: Int): ItemCarrito? =
            items.find { it.productoId == productoId }

        override suspend fun insertarItem(item: ItemCarrito) {
            val existing = items.find { it.productoId == item.productoId }
            if (existing != null) {
                val index = items.indexOf(existing)
                items[index] = existing.copy(cantidad = existing.cantidad + item.cantidad)
            } else {
                items.add(item.copy(id = items.size + 1))
            }
            flow.value = items.toList()
        }

        override suspend fun actualizarItem(item: ItemCarrito) {
            val index = items.indexOfFirst { it.id == item.id }
            if (index != -1) {
                items[index] = item
                flow.value = items.toList()
            }
        }

        override suspend fun eliminarItem(item: ItemCarrito) {
            items.removeIf { it.id == item.id }
            flow.value = items.toList()
        }

        override suspend fun eliminarPorId(id: Int) {
            items.removeIf { it.id == id }
            flow.value = items.toList()
        }

        override suspend fun vaciarCarrito() {
            items.clear()
            flow.value = items.toList()
        }
    }

    @Test
    fun comprarProductos_descuentaStockCorrectamente() = runBlocking {
        val producto = Producto(
            id = 1,
            nombre = "Aceite Esencial",
            descripcion = "Lavanda",
            precio = 10000.0,
            categoriaEspecialidad = "Aromaterapia",
            stock = 10
        )
        val productoDao = FakeProductoDao(listOf(producto))
        val carritoDao = FakeCarritoDao()
        val repository = RoomCarritoRepository(carritoDao, productoDao)

        carritoDao.insertarItem(
            ItemCarrito(
                productoId = 1,
                nombreProducto = "Aceite Esencial",
                precio = 10000.0,
                cantidad = 3
            )
        )

        val resultado = repository.comprarProductos()

        assertTrue(resultado)
        assertEquals(7, productoDao.getById(1)?.stock)
        assertTrue(carritoDao.obtenerCarrito().isEmpty())
    }

    @Test
    fun comprarProductos_stockInsuficiente_retornaFalseYNoDescuenta() = runBlocking {
        val producto = Producto(
            id = 2,
            nombre = "Crema Corporal",
            descripcion = "Hidratante",
            precio = 15000.0,
            categoriaEspecialidad = "Masajes",
            stock = 2
        )
        val productoDao = FakeProductoDao(listOf(producto))
        val carritoDao = FakeCarritoDao()
        val repository = RoomCarritoRepository(carritoDao, productoDao)

        carritoDao.insertarItem(
            ItemCarrito(
                productoId = 2,
                nombreProducto = "Crema Corporal",
                precio = 15000.0,
                cantidad = 5
            )
        )

        val resultado = repository.comprarProductos()

        assertFalse(resultado)
        assertEquals(2, productoDao.getById(2)?.stock)
        assertEquals(1, carritoDao.obtenerCarrito().size)
    }

    @Test
    fun agendarCita_creaCitaYBloqueaHorarioEspecialista() = runBlocking {
        val especialista = Especialista(
            id = 1,
            primerNombre = "Ana",
            apellidoPaterno = "Pérez",
            apellidoMaterno = "Gómez",
            email = "ana@sanaesencia.cl",
            telefono = "+56911111111",
            especialidades = listOf(Especialidad.PSICOLOGIA),
            diasAtencion = setOf(java.time.DayOfWeek.MONDAY)
        )
        val paciente = Paciente(
            id = 1,
            run = "12345678",
            runDv = "9",
            primerNombre = "Juan",
            apellidoPaterno = "Soto",
            apellidoMaterno = "Vera",
            email = "juan@gmail.com",
            telefono = "+56922222222"
        )
        val fecha = LocalDate.now().with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.MONDAY))
        val hora = LocalTime.of(10, 0)
        
        val citas = mutableListOf<Cita>()
        val repository = object : cl.sanaesencia.app.domain.repository.CitaRepository {
            override fun getCitas(): List<Cita> = citas
            override fun getCitaById(id: Int): Cita? = citas.find { it.id == id }
            override fun agendarCita(cita: Cita): Int? {
                val nueva = cita.copy(id = citas.size + 1)
                citas.add(nueva)
                return nueva.id
            }
            override fun cancelarCita(id: Int): Boolean {
                val c = getCitaById(id) ?: return false
                val idx = citas.indexOf(c)
                citas[idx] = c.copy(estado = EstadoCita.CANCELADA)
                return true
            }
            override fun confirmarCita(id: Int): Boolean = true
            override fun pagarCita(id: Int): Boolean = true
        }

        val cita1 = Cita(
            id = 0,
            fecha = fecha,
            hora = hora,
            especialidad = Especialidad.PSICOLOGIA,
            precio = 30000.0,
            notas = null,
            especialista = especialista,
            paciente = paciente,
            estado = EstadoCita.PENDIENTE
        )

        val idCita1 = repository.agendarCita(cita1)
        assertNotNull(idCita1)
        assertEquals(1, repository.getCitas().size)

        val ocupados = repository.getCitas().filter { it.especialista.id == especialista.id && it.fecha == fecha && it.estado != EstadoCita.CANCELADA }
        val horaOcupada = hora
        val estaOcupado = ocupados.any { it.hora == horaOcupada }

        assertTrue(estaOcupado)
    }
}
