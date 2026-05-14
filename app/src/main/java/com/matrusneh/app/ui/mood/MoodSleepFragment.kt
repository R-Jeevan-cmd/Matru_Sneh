package com.matrusneh.app.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.matrusneh.app.R
import com.matrusneh.app.data.db.AppDatabase
import com.matrusneh.app.data.repository.MoodSleepRepository
import com.matrusneh.app.databinding.FragmentMoodSleepBinding
import java.text.SimpleDateFormat
import java.util.*

class MoodSleepFragment : Fragment() {

    private var _binding: FragmentMoodSleepBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MoodSleepViewModel by viewModels {
        MoodSleepViewModelFactory(MoodSleepRepository(AppDatabase.getDatabase(requireContext()).moodSleepDao()))
    }

    private var selectedMood = 3
    private var selectedEmoji = "😐"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodSleepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        binding.tvCurrentDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())

        setupMoodPicker()
        
        binding.sleepSlider.addOnChangeListener { _, value, _ ->
            binding.tvSleepHours.text = getString(R.string.sleep_hours_count, value)
            if (value < 6.0f) {
                Toast.makeText(requireContext(), R.string.low_sleep_warning, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSaveJournal.setOnClickListener {
            val sleepQuality = when (binding.sleepQualityGroup.checkedChipId) {
                R.id.chipPoor -> 1
                R.id.chipOkay -> 2
                R.id.chipGood -> 3
                else -> 2
            }
            val notes = binding.etNotes.text.toString()

            viewModel.saveLog(currentDate, selectedMood, selectedEmoji, binding.sleepSlider.value, sleepQuality, notes)
            Toast.makeText(requireContext(), "Journal saved", Toast.LENGTH_SHORT).show()
        }

        setupRecyclerView()
        observeViewModel()
        viewModel.loadTodayLog(currentDate)
    }

    private fun setupMoodPicker() {
        val moods = listOf(
            Triple(binding.mood1, 1, "😢"),
            Triple(binding.mood2, 2, "😕"),
            Triple(binding.mood3, 3, "😐"),
            Triple(binding.mood4, 4, "🙂"),
            Triple(binding.mood5, 5, "😄")
        )

        moods.forEach { (view, score, emoji) ->
            view.setOnClickListener {
                updateMoodSelection(view, score, emoji)
            }
        }
        updateMoodSelection(binding.mood3, 3, "😐")
    }

    private fun updateMoodSelection(view: TextView, score: Int, emoji: String) {
        listOf(binding.mood1, binding.mood2, binding.mood3, binding.mood4, binding.mood5).forEach {
            it.setBackgroundResource(R.drawable.bg_mood_unselected)
            it.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
        }
        view.setBackgroundResource(R.drawable.bg_mood_selected)
        view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).start()
        selectedMood = score
        selectedEmoji = emoji
        binding.tvMoodLabel.text = when(score) {
            1 -> "Very Sad"
            2 -> "Sad"
            3 -> "Okay"
            4 -> "Happy"
            5 -> "Very Happy"
            else -> "Okay"
        }
    }

    private fun setupRecyclerView() {
        binding.rvJournalHistory.layoutManager = LinearLayoutManager(requireContext())
        val adapter = MoodSleepAdapter()
        binding.rvJournalHistory.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.todayLog.observe(viewLifecycleOwner) { log ->
            log?.let {
                binding.sleepSlider.value = it.sleepHours
                binding.etNotes.setText(it.notes)
                // Select proper mood and sleep quality chips
                when(it.sleepQuality) {
                    1 -> binding.chipPoor.isChecked = true
                    2 -> binding.chipOkay.isChecked = true
                    3 -> binding.chipGood.isChecked = true
                }
            }
        }
        
        viewModel.allLogs.observe(viewLifecycleOwner) { logs ->
            (binding.rvJournalHistory.adapter as MoodSleepAdapter).submitList(logs)
            
            // Check for low mood pattern
            val lowMoodCount = logs.take(3).count { it.moodScore <= 2 }
            if (lowMoodCount >= 3) {
                Toast.makeText(requireContext(), R.string.low_mood_warning, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
