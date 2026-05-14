package com.matrusneh.app.data.repository

import androidx.lifecycle.LiveData
import com.matrusneh.app.data.db.dao.MoodSleepDao
import com.matrusneh.app.data.db.entities.MoodSleepLog

class MoodSleepRepository(private val moodSleepDao: MoodSleepDao) {
    val allLogs: LiveData<List<MoodSleepLog>> = moodSleepDao.getAllLogs()

    suspend fun insertOrUpdate(log: MoodSleepLog) {
        val existing = moodSleepDao.getByDate(log.date)
        if (existing == null) {
            moodSleepDao.insert(log)
        } else {
            moodSleepDao.update(log.copy(id = existing.id))
        }
    }

    suspend fun getLogByDate(date: String) = moodSleepDao.getByDate(date)
    
    suspend fun getWeeklyAvgMood() = moodSleepDao.getWeeklyAvgMood()
    suspend fun getWeeklyAvgSleep() = moodSleepDao.getWeeklyAvgSleep()
}
