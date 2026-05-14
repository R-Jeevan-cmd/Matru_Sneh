package com.matrusneh.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.matrusneh.app.data.db.dao.*
import com.matrusneh.app.data.db.entities.*

@Database(
    entities = [
        KickEvent::class,
        CheckupReminder::class,
        NutritionLog::class,
        DangerSignLog::class,
        VitalsRecord::class,
        MoodSleepLog::class
    ],
    version = 4, // Bumped from 3 to 4 to apply NutritionLog PrimaryKey change
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun kickDao(): KickDao
    abstract fun checkupDao(): CheckupDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun dangerSignDao(): DangerSignDao
    abstract fun vitalsDao(): VitalsDao
    abstract fun moodSleepDao(): MoodSleepDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "matru_sneh_database"
                )
                .fallbackToDestructiveMigration() // Important for development to apply schema changes
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
