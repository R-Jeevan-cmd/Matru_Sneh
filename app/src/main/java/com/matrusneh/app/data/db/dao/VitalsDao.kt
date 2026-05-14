package com.matrusneh.app.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matrusneh.app.data.db.entities.VitalsRecord

@Dao
interface VitalsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: VitalsRecord)

    @Query("SELECT * FROM vitals_record ORDER BY date DESC")
    fun getAllVitals(): LiveData<List<VitalsRecord>>

    @Query("SELECT * FROM vitals_record WHERE date = :date LIMIT 1")
    suspend fun getVitalsByDate(date: String): VitalsRecord?

    @Query("SELECT * FROM vitals_record ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentVitals(limit: Int): List<VitalsRecord>
}
