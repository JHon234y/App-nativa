

package com.agritrack.native.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agritrack.native.data.database.Harvest
import com.agritrack.native.data.database.HarvestDao
import com.agritrack.native.data.database.WeightRecord
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Estado de la UI para la pantalla de detalles.
 * Agrupa todos los datos necesarios para renderizar la pantalla.
 */
data class HarvestDetailUiState(
    val harvest: Harvest? = null,
    val recordsForSelectedDate: Map<String, List<WeightRecord>> = emptyMap(),
    val availableDates: List<String> = emptyList(),
    val selectedDate: String = "",
    val totalWeightForDate: Double = 0.0
)

class HarvestDetailViewModel(
    private val dao: HarvestDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val harvestId: Int = checkNotNull(savedStateHandle["harvestId"])

    // Flujo privado para la fecha seleccionada actualmente
    private val _selectedDate = MutableStateFlow(getTodayDateString())

    // Flujo de la cosecha actual
    private val _harvest = dao.getHarvestById(harvestId).stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Flujo de todos los registros para la cosecha actual
    private val _records = dao.getRecordsForHarvest(harvestId).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // El StateFlow principal que la UI observará
    val uiState: StateFlow<HarvestDetailUiState> = combine(
        _harvest, _records, _selectedDate
    ) { harvest, records, date ->
        val dates = records.map { it.date }.distinct().sortedDescending()
        val recordsForDate = records.filter { it.date == date }.groupBy { it.workerName }
        val totalWeight = recordsForDate.values.flatten().sumOf { it.weight }

        HarvestDetailUiState(
            harvest = harvest,
            recordsForSelectedDate = recordsForDate,
            availableDates = dates,
            selectedDate = date,
            totalWeightForDate = totalWeight
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HarvestDetailUiState())

    /**
     * Actualiza la lista de trabajadores de la cosecha.
     * Incluye la lógica para renombrar trabajadores en los registros existentes.
     */
    fun updateWorkers(workerList: String) {
        val currentHarvest = _harvest.value ?: return
        val oldWorkers = currentHarvest.workers
        val newWorkers = workerList.split('\n').map { it.trim() }.filter { it.isNotBlank() }

        viewModelScope.launch {
            // Lógica de renombrado
            val removed = oldWorkers.filterNot(newWorkers::contains)
            val added = newWorkers.filterNot(oldWorkers::contains)
            if (removed.size == 1 && added.size == 1) {
                val oldName = removed.first()
                val newName = added.first()
                _records.value.filter { it.workerName == oldName }.forEach { record ->
                    dao.insertWeightRecord(record.copy(workerName = newName))
                }
            }

            dao.updateHarvest(currentHarvest.copy(workers = newWorkers))
        }
    }

    /**
     * Añade un nuevo registro de peso para el día de hoy.
     */
    fun addWeightRecord(workerName: String, weightStr: String) {
        val weight = weightStr.toDoubleOrNull() ?: return
        if (workerName.isBlank() || weight <= 0) return

        viewModelScope.launch {
            val record = WeightRecord(
                harvestId = harvestId,
                workerName = workerName,
                weight = weight,
                date = getTodayDateString()
            )
            dao.insertWeightRecord(record)
        }
    }

    /**
     * Elimina un registro de peso específico.
     */
    fun deleteWeightRecord(recordId: Int) {
        viewModelScope.launch {
            dao.deleteWeightRecordById(recordId)
        }
    }

    /**
     * Actualiza la fecha seleccionada para mostrar sus registros.
     */
    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
