package com.matrusneh.app.ui.vitals

import androidx.lifecycle.*
import com.matrusneh.app.data.db.entities.VitalsRecord
import com.matrusneh.app.data.repository.VitalsRepository
import kotlinx.coroutines.launch

class VitalsViewModel(private val repository: VitalsRepository) : ViewModel() {

    val allVitals: LiveData<List<VitalsRecord>> = repository.allVitals

    fun insert(weight: Float, systolic: Int, diastolic: Int, date: String) {
        val isHigh = repository.detectHighBP(systolic, diastolic)
        val record = VitalsRecord(
            date = date,
            weightKg = weight,
            systolic = systolic,
            diastolic = diastolic,
            isBPHigh = isHigh
        )
        viewModelScope.launch {
            repository.insert(record)
        }
    }

    fun isBPHigh(systolic: Int, diastolic: Int): Boolean {
        return repository.detectHighBP(systolic, diastolic)
    }
}

class VitalsViewModelFactory(private val repository: VitalsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VitalsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VitalsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
