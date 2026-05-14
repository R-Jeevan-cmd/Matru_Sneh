package com.matrusneh.app.data.repository

import com.matrusneh.app.data.db.dao.KickDao
import com.matrusneh.app.data.db.entities.KickEvent
import kotlinx.coroutines.flow.Flow

class KickRepository(private val kickDao: KickDao) {
    val allKicks: Flow<List<KickEvent>> = kickDao.getAllKicks()

    fun getKicksByDate(date: String): Flow<List<KickEvent>> = kickDao.getKicksByDate(date)

    suspend fun insert(kick: KickEvent) {
        kickDao.insert(kick)
    }
}
