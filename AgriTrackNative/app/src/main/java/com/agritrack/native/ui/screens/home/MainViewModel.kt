
package com.agritrack.native.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agritrack.native.data.database.Harvest
import com.agritrack.native.data.database.HarvestDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel para la HomeScreen.
 * Contiene la lógica de negocio para interactuar con la lista de cosechas.
 */
class MainViewModel(private val dao: HarvestDao) : ViewModel() {

    /**
     * Un StateFlow que expone la lista de todas las cosechas desde la base de datos.
     * La UI observará este flujo para actualizarse automáticamente cuando los datos cambien.
     * `stateIn` convierte el Flow frío del DAO en un Flow caliente (StateFlow) que puede ser
     * compartido por múltiples observadores y mantiene el último valor.
     */
    val harvests: StateFlow<List<Harvest>> = dao.getAllHarvests()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L), // El flujo se activa cuando la UI está visible
            initialValue = emptyList() // Valor inicial mientras se cargan los datos
        )

    /**
     * Crea una nueva cosecha y la inserta en la base de datos.
     * Se ejecuta en una corrutina para no bloquear el hilo principal.
     *
     * @param name El nombre de la nueva cosecha.
     */
    fun createHarvest(name: String) {
        if (name.isBlank()) return // Evita crear cosechas sin nombre

        viewModelScope.launch {
            val newHarvest = Harvest(
                name = name.trim(),
                startDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date()),
                workers = emptyList()
            )
            dao.insertHarvest(newHarvest)
        }
    }

    /**
     * Elimina una cosecha y todos sus registros asociados.
     * (La eliminación en cascada se configuraría en la base de datos para los registros).
     */
    fun deleteHarvest(harvest: Harvest) {
        viewModelScope.launch {
            dao.deleteHarvestById(harvest.id)
            // Aquí también se deberían eliminar los WeightRecord asociados a esta cosecha
            // para mantener la integridad de los datos.
        }
    }
}
