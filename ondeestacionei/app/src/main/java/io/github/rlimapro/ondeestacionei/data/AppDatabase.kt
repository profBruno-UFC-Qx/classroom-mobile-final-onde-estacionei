package io.github.rlimapro.ondeestacionei.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.rlimapro.ondeestacionei.data.dao.ParkingDao
import io.github.rlimapro.ondeestacionei.model.ParkingLocation

@Database(entities = [ParkingLocation::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun parkingDao(): ParkingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "onde_estacionei_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}