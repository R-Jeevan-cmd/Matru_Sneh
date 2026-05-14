package com.matrusneh.app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_logs")
data class NutritionLog(
    @PrimaryKey val date: String, // format "yyyy-MM-dd"
    val ragiEaten: Boolean = false,
    val greensEaten: Boolean = false,
    val pulsesEaten: Boolean = false,
    val milkEaten: Boolean = false,
    val fruitsEaten: Boolean = false,
    val waterGlasses: Int = 0
)
