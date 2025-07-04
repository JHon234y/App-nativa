package com.agritrack.app.ui.screens.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.agritrack.app.data.database.WeightRecord

@Composable
fun HarvestDetailScreen(
    viewModel: HarvestDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.harvest?.name ?: "Cargando...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { WorkersPanel(uiState.harvest?.workers.orEmpty(), viewModel::updateWorkers) }
            item { AddRecordPanel(uiState.harvest?.workers.orEmpty(), viewModel::addWeightRecord) }
            item { DateList(uiState.availableDates, uiState.selectedDate, viewModel::selectDate) }
            item { RecordsTable(uiState.recordsForSelectedDate, viewModel::deleteWeightRecord) }
        }
    }
}

@Composable
private fun WorkersPanel(workers: List<String>, onSave: (String) -> Unit) {
    var text by remember(workers) { mutableStateOf(workers.joinToString("\n")) }
    Column {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Trabajadores (uno por línea)") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onSave(text) }, modifier = Modifier.align(Alignment.End)) {
            Text("Guardar Trabajadores")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddRecordPanel(workers: List<String>, onAdd: (String, String) -> Unit) {
    var selectedWorker by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedWorker,
                onValueChange = {},
                readOnly = true,
                label = { Text("Seleccionar Trabajador") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                workers.forEach { worker ->
                    DropdownMenuItem(text = { Text(worker) }, onClick = {
                        selectedWorker = worker
                        expanded = false
                    })
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onAdd(selectedWorker, weight) }, modifier = Modifier.align(Alignment.End)) {
            Text("Añadir Registro")
        }
    }
}

@Composable
private fun DateList(dates: List<String>, selectedDate: String, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(dates) {
            date ->
            FilterChip(
                selected = date == selectedDate,
                onClick = { onSelect(date) },
                label = { Text(date) }
            )
        }
    }
}

@Composable
private fun RecordsTable(records: Map<String, List<WeightRecord>>, onDelete: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        records.forEach { (workerName, weights) ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(workerName, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    weights.forEach { record ->
                        AnimatedVisibility(visible = true) { // Can be tied to a state for animations
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${record.weight} kg", modifier = Modifier.weight(1f))
                                IconButton(onClick = { onDelete(record.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
