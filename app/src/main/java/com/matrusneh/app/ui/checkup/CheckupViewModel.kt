package com.matrusneh.app.ui.checkup

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import com.matrusneh.app.data.db.entities.CheckupReminder
import com.matrusneh.app.data.repository.CheckupRepository
import com.matrusneh.app.worker.CheckupReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CheckupViewModel(
    application: Application,
    private val repository: CheckupRepository
) : AndroidViewModel(application) {

    val upcomingCheckups: LiveData<List<CheckupReminder>> = 
        repository.getUpcomingCheckups(System.currentTimeMillis()).asLiveData()

    val allCheckups: LiveData<List<CheckupReminder>> = repository.allCheckups.asLiveData()

    fun addCheckup(name: String, date: Long) {
        viewModelScope.launch {
            val id = repository.insert(CheckupReminder(checkupName = name, checkupDate = date))
            scheduleReminder(id.toInt(), name, date)
        }
    }

    fun markDone(checkup: CheckupReminder) {
        viewModelScope.launch {
            repository.update(checkup.copy(isCompleted = true))
            cancelReminder(checkup.id)
        }
    }

    private fun scheduleReminder(id: Int, name: String, date: Long) {
        val delay = (date - TimeUnit.DAYS.toMillis(1)) - System.currentTimeMillis()
        if (delay > 0) {
            val data = workDataOf("checkup_name" to name)
            val request = OneTimeWorkRequestBuilder<CheckupReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("reminder_$id")
                .build()

            WorkManager.getInstance(getApplication()).enqueueUniqueWork(
                "checkup_$id",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    private fun cancelReminder(id: Int) {
        WorkManager.getInstance(getApplication()).cancelUniqueWork("checkup_$id")
    }
}

class CheckupViewModelFactory(
    private val application: Application,
    private val repository: CheckupRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CheckupViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
