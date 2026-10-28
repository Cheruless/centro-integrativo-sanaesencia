package cl.sanaesencia.app.ui.productos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.domain.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetalleScreen(
    producto: Producto,
    onBackClick: () -> Unit,
    onActionClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = producto.nombre) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onActionClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Añadir al carro")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
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

            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Categoría: ${producto.categoriaEspecialidad}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "$ ${producto.precio}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Stock disponible: ${producto.stock}",
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = producto.descripcion,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
