package com.matrusneh.app.ui.kick

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.matrusneh.app.R
import com.matrusneh.app.databinding.ItemKickStatBinding

class KickWeeklyAdapter : ListAdapter<KickStat, KickWeeklyAdapter.KickViewHolder>(KickDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KickViewHolder {
        val binding = ItemKickStatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KickViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KickViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class KickViewHolder(private val binding: ItemKickStatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stat: KickStat) {
            binding.tvDate.text = stat.date
            binding.tvKicksPerHour.text = "${stat.kicksPerHour} kicks/hr"
            
            if (stat.isHealthy) {
                binding.statusChip.text = "Good"
                binding.statusChip.chipBackgroundColor = ContextCompat.getColorStateList(binding.root.context, android.R.color.holo_green_light)
                binding.statusChip.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorSecondary))
            } else {
                binding.statusChip.text = "Low"
                binding.statusChip.chipBackgroundColor = ContextCompat.getColorStateList(binding.root.context, android.R.color.holo_red_light)
                binding.statusChip.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorDanger))
            }
        }
    }

    class KickDiffCallback : DiffUtil.ItemCallback<KickStat>() {
        override fun areItemsTheSame(oldItem: KickStat, newItem: KickStat): Boolean = oldItem.date == newItem.date
        override fun areContentsTheSame(oldItem: KickStat, newItem: KickStat): Boolean = oldItem == newItem
    }
}
