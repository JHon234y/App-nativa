package com.agritrack.native.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agritrack.native.data.database.Harvest

/**
 * La pantalla principal que muestra la lista de cosechas.
 */
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onHarvestClick: (Int) -> Unit // Callback para navegar al detalle de la cosecha
) {
    // Se suscribe al StateFlow del ViewModel. Compose se encargará de redibujar
    // automáticamente cuando la lista de cosechas cambie.
    val harvests by viewModel.harvests.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AgriTrack Pro") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear Nueva Cosecha")
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(visible = harvests.isEmpty()) {
            EmptyState()
        }
        AnimatedVisibility(visible = harvests.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(harvests, key = { it.id }) {
                    harvest ->
                    HarvestCard(harvest = harvest, onClick = { onHarvestClick(harvest.id) })
                }
            }
        }
    }

    if (showDialog) {
        NewHarvestDialog(
            onDismiss = { showDialog = false },
            onCreate = {
                viewModel.createHarvest(it)
                showDialog = false
            }
        )
    }
}

/**
 * Una tarjeta que muestra la información resumida de una cosecha.
 */
@Composable
fun HarvestCard(harvest: Harvest, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = "Cosecha",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = harvest.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = "Fecha", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = harvest.startDate, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/**
 * El diálogo para crear una nueva cosecha.
 */
@Composable
fun NewHarvestDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🌱 Nueva Cosecha") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la cosecha") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onCreate(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Mensaje que se muestra cuando no hay cosechas.
 */
@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "No hay cosechas.\n¡Crea una con el botón +!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
