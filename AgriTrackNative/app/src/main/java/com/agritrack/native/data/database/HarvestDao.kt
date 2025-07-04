
package com.agritrack.native.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para las entidades de la base de datos de AgriTrack.
 * Define todas las operaciones de base de datos (consultas, inserciones, actualizaciones, etc.).
 */
@Dao
interface HarvestDao {

    // --- Operaciones para Cosechas (Harvests) ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHarvest(harvest: Harvest)

    @Update
    suspend fun updateHarvest(harvest: Harvest)

    @Query("SELECT * FROM harvests ORDER BY id DESC")
    fun getAllHarvests(): Flow<List<Harvest>> // Flow para actualizaciones automáticas en la UI

    @Query("SELECT * FROM harvests WHERE id = :harvestId")
    suspend fun getHarvestById(harvestId: Int): Harvest?

    @Query("DELETE FROM harvests WHERE id = :harvestId")
    suspend fun deleteHarvestById(harvestId: Int)

    // --- Operaciones para Registros de Peso (WeightRecords) ---

    @Insert
    suspend fun insertWeightRecord(record: WeightRecord)

    @Query("SELECT * FROM weight_records WHERE harvestId = :harvestId ORDER BY date DESC, id DESC")
    fun getRecordsForHarvest(harvestId: Int): Flow<List<WeightRecord>>

    @Query("DELETE FROM weight_records WHERE id = :recordId")
    suspend fun deleteWeightRecordById(recordId: Int)

    // Se podrían añadir más consultas específicas si fueran necesarias, como:
    // - Obtener todos los registros de un trabajador específico.
    // - Obtener un resumen de pesos por fecha.
}
