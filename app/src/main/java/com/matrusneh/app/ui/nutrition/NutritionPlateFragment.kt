package com.matrusneh.app.ui.nutrition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.matrusneh.app.R
import com.matrusneh.app.data.db.AppDatabase
import com.matrusneh.app.data.repository.NutritionRepository
import com.matrusneh.app.databinding.FragmentNutritionPlateBinding
import com.matrusneh.app.databinding.ItemFoodCardBinding
import java.text.SimpleDateFormat
import java.util.*

class NutritionPlateFragment : Fragment() {

    private var _binding: FragmentNutritionPlateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NutritionViewModel by viewModels {
        NutritionViewModelFactory(NutritionRepository(AppDatabase.getDatabase(requireContext()).nutritionDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNutritionPlateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateSelector()
        setupFoodGrid()

        binding.btnPlusWater.setOnClickListener { viewModel.updateWater(1) }
        binding.btnMinusWater.setOnClickListener { viewModel.updateWater(-1) }

        binding.fabSaveNutrition.setOnClickListener {
            viewModel.nutritionLog.value?.let {
                viewModel.saveLog(it)
                Toast.makeText(requireContext(), "Nutrition saved!", Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()
    }

    private fun setupDateSelector() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displaySdf = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        binding.dateChipGroup.removeAllViews()
        for (i in 0 until 7) {
            val date = calendar.time
            val dateStr = sdf.format(date)
            val chip = Chip(requireContext())
            chip.text = if (i == 0) "Today" else displaySdf.format(date)
            chip.isCheckable = true
            chip.id = View.generateViewId()
            if (i == 0) {
                chip.isChecked = true
                viewModel.setSelectedDate(dateStr)
            }
            
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setSelectedDate(dateStr)
                }
            }
            binding.dateChipGroup.addView(chip)
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
    }

    private fun setupFoodGrid() {
        binding.rvFoodGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFoodGrid.adapter = FoodAdapter { foodType ->
            viewModel.toggleFood(foodType)
        }
    }

    private fun observeViewModel() {
        viewModel.nutritionLog.observe(viewLifecycleOwner) { log ->
            val displayLog = log ?: com.matrusneh.app.data.db.entities.NutritionLog(date = "")
            (binding.rvFoodGrid.adapter as FoodAdapter).updateLog(displayLog)
            binding.tvWaterCount.text = getString(R.string.glasses_count, displayLog.waterGlasses)
            
            var score = 0
            if (displayLog.ragiEaten) score++
            if (displayLog.greensEaten) score++
            if (displayLog.pulsesEaten) score++
            if (displayLog.milkEaten) score++
            if (displayLog.fruitsEaten) score++
            
            binding.tvNutritionScore.text = "$score/5"
            binding.nutritionProgress.progress = score * 20
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class FoodAdapter(private val onToggle: (String) -> Unit) : RecyclerView.Adapter<FoodViewHolder>() {
        
        private var currentLog: com.matrusneh.app.data.db.entities.NutritionLog? = null
        
        private val foodItems = listOf(
            Triple("Ragi", R.drawable.ic_ragi, "hasRagi"),
            Triple("Greens", R.drawable.ic_greens, "hasGreens"),
            Triple("Pulses", R.drawable.ic_pulses, "hasPulses"),
            Triple("Milk/Curd", R.drawable.ic_milk, "hasMilk"),
            Triple("Fruits", R.drawable.ic_fruits, "hasFruits")
        )

        fun updateLog(log: com.matrusneh.app.data.db.entities.NutritionLog) {
            currentLog = log
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
            val binding = ItemFoodCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FoodViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
            val item = foodItems[position]
            holder.binding.tvFoodName.text = item.first
            holder.binding.ivFoodIcon.setImageResource(item.second)
            
            val isChecked = when(item.third) {
                "hasRagi" -> currentLog?.ragiEaten ?: false
                "hasGreens" -> currentLog?.greensEaten ?: false
                "hasPulses" -> currentLog?.pulsesEaten ?: false
                "hasMilk" -> currentLog?.milkEaten ?: false
                "hasFruits" -> currentLog?.fruitsEaten ?: false
                else -> false
            }

            if (isChecked) {
                holder.binding.layoutBackground.setBackgroundResource(R.drawable.bg_food_card_checked)
                holder.binding.ivChecked.visibility = View.VISIBLE
            } else {
                holder.binding.layoutBackground.setBackgroundResource(R.drawable.bg_food_card_unchecked)
                holder.binding.ivChecked.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                onToggle(item.third)
            }
        }

        override fun getItemCount() = foodItems.size
    }

    private class FoodViewHolder(val binding: ItemFoodCardBinding) : RecyclerView.ViewHolder(binding.root)
}
