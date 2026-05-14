package com.matrusneh.app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vitals_record")
data class VitalsRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,           // "yyyy-MM-dd"
    val weightKg: Float,
    val systolic: Int,          // upper BP e.g. 120
    val diastolic: Int,         // lower BP e.g. 80
    val isBPHigh: Boolean       // systolic>140 || diastolic>90
)
