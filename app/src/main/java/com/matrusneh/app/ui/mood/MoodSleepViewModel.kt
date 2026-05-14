package com.matrusneh.app.ui.mood

import androidx.lifecycle.*
import com.matrusneh.app.data.db.entities.MoodSleepLog
import com.matrusneh.app.data.repository.MoodSleepRepository
import kotlinx.coroutines.launch

class MoodSleepViewModel(private val repository: MoodSleepRepository) : ViewModel() {

    val allLogs: LiveData<List<MoodSleepLog>> = repository.allLogs
    
    private val _todayLog = MutableLiveData<MoodSleepLog?>()
    val todayLog: LiveData<MoodSleepLog?> = _todayLog

    fun loadTodayLog(date: String) {
        viewModelScope.launch {
            _todayLog.value = repository.getLogByDate(date)
        }
    }

    fun saveLog(
        date: String,
        moodScore: Int,
        moodEmoji: String,
        sleepHours: Float,
        sleepQuality: Int,
        notes: String
    ) {
        viewModelScope.launch {
            val log = MoodSleepLog(
                date = date,
                moodScore = moodScore,
                moodEmoji = moodEmoji,
                sleepHours = sleepHours,
                sleepQuality = sleepQuality,
                notes = notes
            )
            repository.insertOrUpdate(log)
            loadTodayLog(date)
        }
    }
}

class MoodSleepViewModelFactory(private val repository: MoodSleepRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoodSleepViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoodSleepViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
