
package com.agritrack.native.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * La clase principal de la base de datos para la aplicación.
 * Esta clase abstracta extiende RoomDatabase y sirve como el punto de acceso principal
 * a la base de datos persistente de la aplicación.
 */
@Database(
    entities = [Harvest::class, WeightRecord::class], // Todas las tablas de la base de datos
    version = 1, // Versión de la base de datos, para migraciones futuras
    exportSchema = false // No exportar el esquema a un archivo JSON
)
@TypeConverters(Converters::class) // Registra los convertidores de tipo
abstract class AppDatabase : RoomDatabase() {

    /**
     * Proporciona acceso al DAO para las cosechas.
     * Room implementará esta función abstracta por nosotros.
     */
    abstract fun harvestDao(): HarvestDao

    /**
     * Companion object para permitir el acceso a los métodos de creación de la base de datos
     * sin necesidad de instanciar la clase. Usamos el patrón Singleton aquí.
     */
    companion object {
        // La anotación @Volatile asegura que el valor de INSTANCE sea siempre el más reciente
        // y visible para todos los hilos de ejecución.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia Singleton de la base de datos.
         *
         * @param context El contexto de la aplicación.
         * @return La instancia Singleton de AppDatabase.
         */
        fun getDatabase(context: Context): AppDatabase {
            // Si la instancia ya existe, la devuelve.
            // Si no, crea la base de datos en un bloque sincronizado para evitar
            // condiciones de carrera entre múltiples hilos.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agritrack_database" // Nombre del archivo de la base de datos
                ).build()
                INSTANCE = instance
                // Devuelve la instancia recién creada
                instance
            }
        }
    }
}
