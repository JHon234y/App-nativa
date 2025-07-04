
package com.agritrack.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Define la tabla 'harvests' en la base de datos.
 * Cada instancia de esta clase representa una cosecha.
 */
@Entity(tableName = "harvests")
data class Harvest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val startDate: String,
    val workers: List<String> // Room usará el TypeConverter para guardar esta lista
)

/**
 * Define la tabla 'weight_records' en la base de datos.
 * Cada instancia representa un único registro de peso para un trabajador en una fecha específica.
 */
@Entity(tableName = "weight_records")
data class WeightRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val harvestId: Int, // Clave foránea que vincula este registro a una Harvest
    val workerName: String,
    val weight: Double,
    val date: String // Formato "YYYY-MM-DD"
)

/**
 * Proporciona a Room la capacidad de convertir tipos complejos (como List<String>)
 * a un formato que pueda almacenar en la base de datos (String JSON).
 */
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return gson.toJson(list)
    }
}
