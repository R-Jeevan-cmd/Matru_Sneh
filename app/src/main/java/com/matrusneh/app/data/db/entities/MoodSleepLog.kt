package com.matrusneh.app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_sleep_log")
data class MoodSleepLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,           // "yyyy-MM-dd"
    val moodScore: Int,         // 1-5 (1=very sad, 5=very happy)
    val moodEmoji: String,      // "😢", "😕", "😐", "🙂", "😄"
    val sleepHours: Float,      // 0.0 to 12.0
    val sleepQuality: Int,      // 1-3 (1=poor, 2=ok, 3=good)
    val notes: String           // optional free text
)
