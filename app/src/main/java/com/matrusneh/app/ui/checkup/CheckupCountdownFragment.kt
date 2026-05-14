package com.matrusneh.app.ui.checkup

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.matrusneh.app.R
import com.matrusneh.app.data.db.AppDatabase
import com.matrusneh.app.data.db.entities.CheckupReminder
import com.matrusneh.app.data.repository.CheckupRepository
import com.matrusneh.app.databinding.FragmentCheckupCountdownBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CheckupCountdownFragment : Fragment() {

    private var _binding: FragmentCheckupCountdownBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CheckupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckupCountdownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = CheckupRepository(database.checkupDao())
        val factory = CheckupViewModelFactory(requireActivity().application, repository)
        viewModel = ViewModelProvider(this, factory)[CheckupViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        binding.fabAddCheckup.setOnClickListener {
            showAddCheckupDialog()
        }

        showBabyGrowthDialog()
    }

    private fun setupRecyclerView() {
        binding.rvCheckups.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCheckups.adapter = CheckupAdapter { checkup ->
            viewModel.markDone(checkup)
        }
    }

    private fun observeViewModel() {
        viewModel.upcomingCheckups.observe(viewLifecycleOwner) { checkups ->
            if (checkups.isNotEmpty()) {
                val next = checkups[0]
                binding.tvNextCheckupName.text = next.checkupName
                val diff = next.checkupDate - System.currentTimeMillis()
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                
                if (days < 0) {
                    binding.tvCountdown.text = getString(R.string.overdue)
                    binding.tvCountdown.setTextColor(resources.getColor(R.color.danger, null))
                } else if (days == 0L) {
                    binding.tvCountdown.text = getString(R.string.today)
                    binding.tvCountdown.setTextColor(resources.getColor(R.color.danger, null))
                } else {
                    binding.tvCountdown.text = getString(R.string.days_remaining, days.toInt())
                    binding.tvCountdown.setTextColor(resources.getColor(R.color.white, null))
                }
            } else {
                binding.tvNextCheckupName.text = "No upcoming checkups"
                binding.tvCountdown.text = "--"
            }
        }

        viewModel.allCheckups.observe(viewLifecycleOwner) { all ->
            val completed = all.count { it.isCompleted }
            binding.progressCheckups.max = if (all.isEmpty()) 1 else all.size
            binding.progressCheckups.progress = completed
            (binding.rvCheckups.adapter as? CheckupAdapter)?.submitList(all)
        }
    }

    private fun showAddCheckupDialog() {
        val etName = EditText(requireContext()).apply { hint = getString(R.string.checkup_name_hint) }
        val calendar = Calendar.getInstance()

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_checkup)
            .setView(etName)
            .setPositiveButton(R.string.select_date) { _, _ ->
                DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val selectedDate = calendar.timeInMillis
                    viewModel.addCheckup(etName.text.toString(), selectedDate)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showBabyGrowthDialog() {
        // Simple logic for week: default to 24 for demo
        val week = 24
        val growthDesc = BabyGrowthData.weeksMap[week] ?: "Stay healthy!"
        
        AlertDialog.Builder(requireContext())
            .setTitle("Weekly Baby Growth")
            .setMessage(growthDesc)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class CheckupAdapter(private val onDone: (CheckupReminder) -> Unit) : 
        RecyclerView.Adapter<CheckupAdapter.ViewHolder>() {

        private var list = listOf<CheckupReminder>()

        fun submitList(newList: List<CheckupReminder>) {
            list = newList
            notifyDataSetChanged()
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_checkup_name)
            val tvDate: TextView = view.findViewById(R.id.tv_checkup_date)
            val cbDone: CheckBox = view.findViewById(R.id.cb_done)
            val chipDays: Chip = view.findViewById(R.id.chip_days)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkup, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.tvName.text = item.checkupName
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            holder.tvDate.text = sdf.format(Date(item.checkupDate))
            holder.cbDone.isChecked = item.isCompleted
            holder.cbDone.isEnabled = !item.isCompleted

            val diff = item.checkupDate - System.currentTimeMillis()
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            holder.chipDays.text = "$days Days"

            holder.cbDone.setOnClickListener {
                if (holder.cbDone.isChecked) {
                    onDone(item)
                }
            }
        }

        override fun getItemCount() = list.size
    }
}
