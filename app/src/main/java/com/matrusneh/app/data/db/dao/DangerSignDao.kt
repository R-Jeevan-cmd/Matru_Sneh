package com.matrusneh.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.matrusneh.app.data.db.entities.DangerSignLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DangerSignDao {
    @Insert
    suspend fun insert(log: DangerSignLog)

    @Query("SELECT * FROM danger_sign_logs WHERE isAcknowledged = 0 ORDER BY timestamp DESC")
    fun getUnacknowledgedSigns(): Flow<List<DangerSignLog>>

    @Query("SELECT * FROM danger_sign_logs ORDER BY timestamp DESC")
    fun getAllSigns(): Flow<List<DangerSignLog>>
}
