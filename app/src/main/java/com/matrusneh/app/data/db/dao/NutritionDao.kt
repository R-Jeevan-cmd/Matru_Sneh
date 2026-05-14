package com.matrusneh.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matrusneh.app.data.db.entities.NutritionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: NutritionLog)

    @Query("SELECT * FROM nutrition_logs WHERE date = :date LIMIT 1")
    fun getLogByDate(date: String): Flow<NutritionLog?>
}
