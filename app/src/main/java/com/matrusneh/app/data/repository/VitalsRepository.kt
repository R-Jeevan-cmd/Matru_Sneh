package com.matrusneh.app.data.repository

import androidx.lifecycle.LiveData
import com.matrusneh.app.data.db.dao.VitalsDao
import com.matrusneh.app.data.db.entities.VitalsRecord

class VitalsRepository(private val vitalsDao: VitalsDao) {
    val allVitals: LiveData<List<VitalsRecord>> = vitalsDao.getAllVitals()

    suspend fun insert(record: VitalsRecord) {
        vitalsDao.insert(record)
    }

    fun detectHighBP(systolic: Int, diastolic: Int): Boolean {
        return systolic > 140 || diastolic > 90
    }
}
