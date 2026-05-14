package com.matrusneh.app.ui.alert

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.matrusneh.app.R
import com.matrusneh.app.data.db.AppDatabase
import com.matrusneh.app.data.db.entities.DangerSignLog
import com.matrusneh.app.data.repository.AlertRepository
import com.matrusneh.app.databinding.DialogDangerAlertBinding
import com.matrusneh.app.databinding.FragmentHealthAlertBinding
import java.text.SimpleDateFormat
import java.util.*

class HealthAlertFragment : Fragment() {

    private var _binding: FragmentHealthAlertBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AlertViewModel

    private val dangerSigns by lazy {
        listOf(
            getString(R.string.sign_headache),
            getString(R.string.sign_vision),
            getString(R.string.sign_swelling),
            getString(R.string.sign_bleeding),
            getString(R.string.sign_no_movement),
            getString(R.string.sign_fever),
            getString(R.string.sign_breathing),
            getString(R.string.sign_stomach_pain)
        )
    }
    private val selectedSigns = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = AlertRepository(database.dangerSignDao())
        val factory = AlertViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AlertViewModel::class.java]

        setupDangerSignsList()
        setupHistoryList()
        
        binding.btnSendAlert.setOnClickListener {
            showConfirmationDialog()
        }

        viewModel.allSigns.observe(viewLifecycleOwner) { signs ->
            (binding.rvAlertHistory.adapter as? AlertHistoryAdapter)?.submitList(signs)
        }
    }

    private fun setupDangerSignsList() {
        binding.rvDangerSigns.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDangerSigns.adapter = DangerSignAdapter(dangerSigns) { sign, isChecked ->
            if (isChecked) selectedSigns.add(sign) else selectedSigns.remove(sign)
            binding.btnSendAlert.isEnabled = selectedSigns.isNotEmpty()
            Log.d("HealthAlert", "Selected: $selectedSigns")
        }
    }

    private fun setupHistoryList() {
        binding.rvAlertHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAlertHistory.adapter = AlertHistoryAdapter()
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.alert_confirm_title)
            .setMessage(R.string.alert_confirm_msg)
            .setPositiveButton("CONFIRM") { _, _ ->
                val symptoms = selectedSigns.toList()
                viewModel.insertDangerSign(symptoms)
                postAlertNotification(symptoms.joinToString(", "))
                showFullScreenAlert()
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun postAlertNotification(symptoms: String) {
        val builder = NotificationCompat.Builder(requireContext(), "matru_sneh_reminders")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.immediate_doctor))
            .setContentText("Reported: $symptoms")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(requireContext())) {
                notify(202, builder.build())
            }
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    private fun showFullScreenAlert() {
        val dialogBinding = DialogDangerAlertBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.tvAlertBody.text = "Aapne ${selectedSigns.joinToString(", ")} report kiya hai. Abhi 108 pe call karein."
        
        dialogBinding.btnCall108.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))
            startActivity(intent)
        }

        dialogBinding.btnCallAsha.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:104"))
            startActivity(intent)
        }

        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class DangerSignAdapter(
        private val signs: List<String>,
        private val onChecked: (String, Boolean) -> Unit
    ) : RecyclerView.Adapter<DangerSignAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val checkBox: MaterialCheckBox = view.findViewById(R.id.cb_danger_sign)
            val tvSymptom: TextView = view.findViewById(R.id.tvSymptom)
            val card: com.google.android.material.card.MaterialCardView = view.findViewById(R.id.cardDanger)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_danger_sign, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val sign = signs[position]
            holder.tvSymptom.text = sign
            
            holder.checkBox.setOnCheckedChangeListener(null)
            holder.checkBox.isChecked = selectedSigns.contains(sign)
            
            updateUi(holder, holder.checkBox.isChecked)

            holder.card.setOnClickListener {
                holder.checkBox.isChecked = !holder.checkBox.isChecked
            }

            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                onChecked(sign, isChecked)
                updateUi(holder, isChecked)
            }
        }

        private fun updateUi(holder: ViewHolder, isChecked: Boolean) {
            if (isChecked) {
                holder.card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.danger))
                holder.tvSymptom.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                holder.checkBox.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                holder.card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                holder.tvSymptom.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                holder.checkBox.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.danger))
            }
        }

        override fun getItemCount() = signs.size
    }

    class AlertHistoryAdapter : RecyclerView.Adapter<AlertHistoryAdapter.ViewHolder>() {
        private var list = listOf<DangerSignLog>()

        fun submitList(newList: List<DangerSignLog>) {
            list = newList
            notifyDataSetChanged()
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvDate: TextView = view.findViewById(R.id.tv_alert_date)
            val tvSymptoms: TextView = view.findViewById(R.id.tv_alert_symptoms)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alert_history, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            holder.tvDate.text = sdf.format(Date(item.timestamp))
            holder.tvSymptoms.text = item.symptomDescription
        }

        override fun getItemCount() = list.size
    }
}
