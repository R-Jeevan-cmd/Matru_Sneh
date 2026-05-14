package com.matrusneh.app.ui.nutrition

import androidx.lifecycle.*
import com.matrusneh.app.R
import com.matrusneh.app.data.db.entities.NutritionLog
import com.matrusneh.app.data.repository.NutritionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class NutritionViewModel(private val repository: NutritionRepository) : ViewModel() {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDate: String = sdf.format(Date())
    
    private val _selectedDate = MutableStateFlow(todayDate)
    val selectedDate: LiveData<String> = _selectedDate.asLiveData()

    // Ensuring the log is always synced with the correctly selected date
    val nutritionLog: LiveData<NutritionLog?> = _selectedDate.flatMapLatest { date ->
        repository.getLogByDate(date)
    }.asLiveData()

    // Daily quote rotation
    private val quotes = listOf(R.string.quote_1, R.string.quote_2)
    val dailyQuoteResId: Int
        get() {
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            return quotes[dayOfYear % quotes.size]
        }

    fun setSelectedDate(date: String) {
        if (_selectedDate.value != date) {
            _selectedDate.value = date
        }
    }

    fun saveLog(log: NutritionLog) {
        viewModelScope.launch {
            // Force the date to match the currently selected date to prevent "carrying over" data
            repository.saveLog(log.copy(date = _selectedDate.value))
        }
    }

    fun updateWater(delta: Int) {
        val current = nutritionLog.value ?: NutritionLog(date = _selectedDate.value)
        val newCount = (current.waterGlasses + delta).coerceAtLeast(0)
        saveLog(current.copy(waterGlasses = newCount))
    }

    fun toggleFood(foodType: String) {
        val current = nutritionLog.value ?: NutritionLog(date = _selectedDate.value)
        val updated = when(foodType) {
            "hasRagi" -> current.copy(ragiEaten = !current.ragiEaten)
            "hasGreens" -> current.copy(greensEaten = !current.greensEaten)
            "hasPulses" -> current.copy(pulsesEaten = !current.pulsesEaten)
            "hasMilk" -> current.copy(milkEaten = !current.milkEaten)
            "hasFruits" -> current.copy(fruitsEaten = !current.fruitsEaten)
            else -> current
        }
        saveLog(updated)
    }
}

class NutritionViewModelFactory(private val repository: NutritionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NutritionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NutritionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
