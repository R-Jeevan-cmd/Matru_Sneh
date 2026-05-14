package com.matrusneh.app.data.repository

import com.matrusneh.app.data.db.dao.DangerSignDao
import com.matrusneh.app.data.db.entities.DangerSignLog
import kotlinx.coroutines.flow.Flow

class AlertRepository(private val dangerSignDao: DangerSignDao) {
    val allSigns: Flow<List<DangerSignLog>> = dangerSignDao.getAllSigns()
    val unacknowledgedSigns: Flow<List<DangerSignLog>> = dangerSignDao.getUnacknowledgedSigns()

    suspend fun insert(log: DangerSignLog) {
        dangerSignDao.insert(log)
    }
}
