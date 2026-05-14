package com.matrusneh.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.matrusneh.app.data.db.entities.KickEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface KickDao {
    @Insert
    suspend fun insert(kick: KickEvent)

    @Query("SELECT * FROM kick_events WHERE sessionDate = :date ORDER BY timestamp DESC")
    fun getKicksByDate(date: String): Flow<List<KickEvent>>

    @Query("SELECT * FROM kick_events ORDER BY timestamp DESC")
    fun getAllKicks(): Flow<List<KickEvent>>
}
