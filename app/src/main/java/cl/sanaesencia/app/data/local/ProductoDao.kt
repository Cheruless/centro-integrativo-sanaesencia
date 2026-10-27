package cl.sanaesencia.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.sanaesencia.app.domain.model.Producto
import kotlinx.coroutines.flow.Flow

/** Consultas para la tabla de productos. */
@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos")
    fun observarTodos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos")
    suspend fun getAll(): List<Producto>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getById(id: Int): Producto?

    @Query("SELECT * FROM productos WHERE categoriaEspecialidad LIKE '%' || :categoria || '%'")
    suspend fun getByCategoria(categoria: String): List<Producto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<Producto>)
}
