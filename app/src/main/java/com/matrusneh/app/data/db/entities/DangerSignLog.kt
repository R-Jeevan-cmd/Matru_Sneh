package com.matrusneh.app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "danger_sign_logs")
data class DangerSignLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val symptomDescription: String,
    val isAcknowledged: Boolean = false
)
