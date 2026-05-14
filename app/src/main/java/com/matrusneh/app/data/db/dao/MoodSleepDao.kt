package com.matrusneh.app.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.matrusneh.app.data.db.entities.MoodSleepLog

@Dao
interface MoodSleepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: MoodSleepLog)

    @Update
    suspend fun update(log: MoodSleepLog)

    @Query("SELECT * FROM mood_sleep_log ORDER BY date DESC")
    fun getAllLogs(): LiveData<List<MoodSleepLog>>

    @Query("SELECT * FROM mood_sleep_log WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): MoodSleepLog?

    @Query("SELECT * FROM mood_sleep_log WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getByDateRange(startDate: String, endDate: String): List<MoodSleepLog>

    @Query("SELECT AVG(moodScore) FROM mood_sleep_log")
    suspend fun getWeeklyAvgMood(): Float

    @Query("SELECT AVG(sleepHours) FROM mood_sleep_log")
    suspend fun getWeeklyAvgSleep(): Float
}
