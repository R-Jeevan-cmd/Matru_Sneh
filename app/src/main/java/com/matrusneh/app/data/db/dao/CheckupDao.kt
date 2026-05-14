package com.matrusneh.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.matrusneh.app.data.db.entities.CheckupReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkup: CheckupReminder): Long

    @Update
    suspend fun update(checkup: CheckupReminder)

    @Query("SELECT * FROM checkup_reminders WHERE checkupDate > :now AND isCompleted = 0 ORDER BY checkupDate ASC")
    fun getUpcomingCheckups(now: Long): Flow<List<CheckupReminder>>

    @Query("SELECT * FROM checkup_reminders ORDER BY checkupDate ASC")
    fun getAllCheckups(): Flow<List<CheckupReminder>>
}
