package com.matrusneh.app.ui.alert

import androidx.lifecycle.*
import com.matrusneh.app.data.db.entities.DangerSignLog
import com.matrusneh.app.data.repository.AlertRepository
import kotlinx.coroutines.launch

class AlertViewModel(private val repository: AlertRepository) : ViewModel() {

    val allSigns: LiveData<List<DangerSignLog>> = repository.allSigns.asLiveData()
    val unacknowledgedSigns: LiveData<List<DangerSignLog>> = repository.unacknowledgedSigns.asLiveData()

    fun insertDangerSign(symptoms: List<String>) {
        viewModelScope.launch {
            val description = symptoms.joinToString(", ")
            repository.insert(DangerSignLog(symptomDescription = description))
        }
    }
}

class AlertViewModelFactory(private val repository: AlertRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlertViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
