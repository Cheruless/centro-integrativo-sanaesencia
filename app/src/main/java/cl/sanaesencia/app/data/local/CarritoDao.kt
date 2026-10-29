package cl.sanaesencia.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.sanaesencia.app.domain.model.ItemCarrito
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {
    @Query("SELECT * FROM carrito")
    fun obtenerCarritoFlow(): Flow<List<ItemCarrito>>

    @Query("SELECT * FROM carrito")
    suspend fun obtenerCarrito(): List<ItemCarrito>

    @Query("SELECT * FROM carrito WHERE productoId = :productoId")
    suspend fun obtenerItemPorProductoId(productoId: Int): ItemCarrito?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarItem(item: ItemCarrito)

    @Update
    suspend fun actualizarItem(item: ItemCarrito)

    @Delete
    suspend fun eliminarItem(item: ItemCarrito)

    @Query("DELETE FROM carrito WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    @Query("DELETE FROM carrito")
    suspend fun vaciarCarrito()
}
