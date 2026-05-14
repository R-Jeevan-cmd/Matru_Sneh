package com.matrusneh.app.ui.kick

import androidx.lifecycle.*
import com.matrusneh.app.data.db.entities.KickEvent
import com.matrusneh.app.data.repository.KickRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class KickStat(val date: String, val kicksPerHour: Int, val isHealthy: Boolean)

class KickViewModel(private val repository: KickRepository) : ViewModel() {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDate: String = sdf.format(Date())

    val kicksToday: LiveData<List<KickEvent>> = repository.getKicksByDate(todayDate).asLiveData()
    
    val weeklyKickData: LiveData<List<KickStat>> = repository.allKicks.asLiveData().map { allKicks ->
        calculateWeeklyStats(allKicks)
    }

    private val _sessionStartTime = MutableLiveData<Long?>(null)
    val sessionStartTime: LiveData<Long?> = _sessionStartTime

    fun startNewSession() {
        _sessionStartTime.value = System.currentTimeMillis()
    }

    fun resetSession() {
        _sessionStartTime.value = null
    }

    fun addKick() {
        viewModelScope.launch {
            repository.insert(KickEvent(sessionDate = todayDate))
        }
    }

    private fun calculateWeeklyStats(allKicks: List<KickEvent>): List<KickStat> {
        val stats = mutableListOf<KickStat>()
        val groupedByDate = allKicks.groupBy { it.sessionDate }
        
        // Get last 7 days
        val calendar = Calendar.getInstance()
        for (i in 0 until 7) {
            val dateStr = sdf.format(calendar.time)
            val dayKicks = groupedByDate[dateStr] ?: emptyList()
            
            // Simplified kicks per hour calculation: 
            // In a real scenario, we'd track active session duration.
            // Here, we'll assume a 2-hour monitoring window as per the prompt's alert logic.
            val count = dayKicks.size
            val kicksPerHour = count / 2 // Assuming 2 hours of tracking
            stats.add(KickStat(dateStr, kicksPerHour, count >= 10))
            
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return stats
    }
}

class KickViewModelFactory(private val repository: KickRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KickViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KickViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
