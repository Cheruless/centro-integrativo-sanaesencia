package cl.sanaesencia.app.ui.productos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.data.ServiceLocator
import cl.sanaesencia.app.domain.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    onMenuClick: () -> Unit = {}
) {
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            productos = ServiceLocator.productoRepository.getProductos()
        }
    }

    if (productoSeleccionado != null) {
        ProductoDetalleScreen(
            producto = productoSeleccionado!!,
            onBackClick = { productoSeleccionado = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Catálogo de Productos") },
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productos, key = { it.id }) { producto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { productoSeleccionado = producto },
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (producto.imagenResId != null) {
                                    Image(
                                        painter = painterResource(id = producto.imagenResId),
                                        contentDescription = producto.nombre,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = producto.nombre,
                                        modifier = Modifier.size(36.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = producto.categoriaEspecialidad.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = producto.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "$ ${producto.precio.toInt()}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
