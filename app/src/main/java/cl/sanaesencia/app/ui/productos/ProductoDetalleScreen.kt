package cl.sanaesencia.app.ui.productos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetalleScreen(
    producto: Producto,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var cantidad by remember { mutableStateOf(1) }
    val sinStock = producto.stock <= 0

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 12.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            if (sinStock) return@ExtendedFloatingActionButton
                            coroutineScope.launch(Dispatchers.IO) {
                                ServiceLocator.carritoRepository.agregarOIncrementar(
                                    productoId = producto.id,
                                    nombre = producto.nombre,
                                    precio = producto.precio,
                                    imagenResId = producto.imagenResId,
                                    cantidad = cantidad
                                )
                                withContext(Dispatchers.Main) {
                                    snackbarHostState.showSnackbar("Añadido al carro: $cantidad x ${producto.nombre}")
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        text = { Text(text = if (sinStock) "Sin stock" else "Añadir al carro") },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = if (sinStock) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                        contentColor = if (sinStock) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
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
                        modifier = Modifier.size(96.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = producto.categoriaEspecialidad.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$ ${producto.precio.toInt()}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Stock disponible: ${producto.stock} unidades",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Descripción del producto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Contador de cantidad
                if (!sinStock) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cantidad",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilledTonalIconButton(
                                onClick = { if (cantidad > 1) cantidad-- },
                                enabled = cantidad > 1
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Restar")
                            }
                            Text(
                                text = cantidad.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            FilledTonalIconButton(
                                onClick = { if (cantidad < producto.stock) cantidad++ },
                                enabled = cantidad < producto.stock
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Sumar")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(bottom = 24.dp))
            }
        }
    }
}
