package cl.sanaesencia.app.ui.productos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pantalla principal del Catálogo de Productos y Insumos de Sanaesencia.
 *
 * TODO (Integrante asignado al Módulo Productos):
 *  1. Crear `ProductoViewModel.kt` en este paquete (`cl.sanaesencia.app.ui.productos`)
 *     utilizando `MockProductoRepository`.
 *  2. Definir la data class `ProductosUiState` (con lista de productos, filtro por categoría temática,
 *     búsqueda por texto y estado de carga).
 *  3. Diseñar cuadrícula o lista de productos (`ProductoCard`) mostrando:
 *     - Nombre del producto e imagen/icono.
 *     - Categoría temática (Aromaterapia, Masajes, Florales, Kinesiología).
 *     - Precio en CLP formateado y stock disponible.
 *     - Botón de acción: "Ver Detalle" o "Consultar Disponibilidad".
 *  4. Implementar barra de búsqueda y filtros por categoría temática.
 */
@Composable
fun ProductosScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Productos y Servicios",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Insumos naturales, aceites esenciales, cremas y esencias florales por categoría.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
