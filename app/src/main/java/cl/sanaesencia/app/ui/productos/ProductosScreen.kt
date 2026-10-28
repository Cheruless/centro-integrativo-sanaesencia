package cl.sanaesencia.app.ui.productos

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.data.ServiceLocator
import cl.sanaesencia.app.domain.model.ItemCarrito
import cl.sanaesencia.app.domain.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    onMenuClick: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    val addedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val scaleStates = remember { mutableStateMapOf<Int, Boolean>() }

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
                    val isAdded = addedStates[producto.id] ?: false
                    val isClicked = scaleStates[producto.id] ?: false
                    val scale by animateFloatAsState(
                        targetValue = if (isClicked) 1.2f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioHighBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )

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
                                    .clip(RoundedCornerShape(12.dp))
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

                            IconButton(
                                onClick = {
                                    scaleStates[producto.id] = true
                                    addedStates[producto.id] = true
                                    coroutineScope.launch {
                                        delay(150)
                                        scaleStates[producto.id] = false
                                        val item = ItemCarrito(
                                            productoId = producto.id,
                                            nombreProducto = producto.nombre,
                                            precio = producto.precio,
                                            cantidad = 1,
                                            imagenResId = producto.imagenResId
                                        )
                                        withContext(Dispatchers.IO) {
                                            ServiceLocator.carritoRepository.insertarItem(item)
                                        }
                                        delay(1000)
                                        addedStates[producto.id] = false
                                    }
                                },
                                modifier = Modifier.scale(scale)
                            ) {
                                Icon(
                                    imageVector = if (isAdded) Icons.Default.Check else Icons.Default.AddShoppingCart,
                                    contentDescription = "Añadir al carro",
                                    tint = if (isAdded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
