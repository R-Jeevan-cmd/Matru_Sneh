package com.matrusneh.app.ui.vitals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.matrusneh.app.R
import com.matrusneh.app.data.db.entities.VitalsRecord
import com.matrusneh.app.databinding.ItemVitalsRowBinding

class VitalsAdapter : ListAdapter<VitalsRecord, VitalsAdapter.VitalsViewHolder>(VitalsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VitalsViewHolder {
        val binding = ItemVitalsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VitalsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VitalsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VitalsViewHolder(private val binding: ItemVitalsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(record: VitalsRecord) {
            binding.tvDate.text = record.date
            binding.tvWeight.text = "${record.weightKg} kg"
            binding.tvBP.text = "${record.systolic}/${record.diastolic}"
            
            if (record.isBPHigh) {
                binding.tvStatusBadge.text = "HIGH"
                binding.tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(binding.root.context, R.color.colorDanger)
            } else {
                binding.tvStatusBadge.text = "Normal"
                binding.tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(binding.root.context, R.color.colorSecondary)
            }
        }
    }

    class VitalsDiffCallback : DiffUtil.ItemCallback<VitalsRecord>() {
        override fun areItemsTheSame(oldItem: VitalsRecord, newItem: VitalsRecord): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: VitalsRecord, newItem: VitalsRecord): Boolean = oldItem == newItem
    }
}
