package com.matrusneh.app.data.repository

import com.matrusneh.app.data.db.dao.CheckupDao
import com.matrusneh.app.data.db.entities.CheckupReminder
import kotlinx.coroutines.flow.Flow

class CheckupRepository(private val checkupDao: CheckupDao) {
    fun getUpcomingCheckups(now: Long): Flow<List<CheckupReminder>> = checkupDao.getUpcomingCheckups(now)
    val allCheckups: Flow<List<CheckupReminder>> = checkupDao.getAllCheckups()

    suspend fun insert(checkup: CheckupReminder): Long = checkupDao.insert(checkup)
    suspend fun update(checkup: CheckupReminder) = checkupDao.update(checkup)
}
