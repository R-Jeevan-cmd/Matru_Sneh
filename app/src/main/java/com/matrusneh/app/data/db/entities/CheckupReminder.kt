package com.matrusneh.app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checkup_reminders")
data class CheckupReminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val checkupName: String,
    val checkupDate: Long,
    val isCompleted: Boolean = false
)
