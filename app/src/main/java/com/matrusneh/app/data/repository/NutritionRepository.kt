package com.matrusneh.app.data.repository

import com.matrusneh.app.data.db.dao.NutritionDao
import com.matrusneh.app.data.db.entities.NutritionLog
import kotlinx.coroutines.flow.Flow

class NutritionRepository(private val nutritionDao: NutritionDao) {
    fun getLogByDate(date: String): Flow<NutritionLog?> = nutritionDao.getLogByDate(date)

    suspend fun saveLog(log: NutritionLog) {
        nutritionDao.insertOrUpdate(log)
    }
}
