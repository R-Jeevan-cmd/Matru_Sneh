package com.matrusneh.app.ui.mood

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.matrusneh.app.data.db.entities.MoodSleepLog
import com.matrusneh.app.databinding.ItemMoodLogBinding

class MoodSleepAdapter : ListAdapter<MoodSleepLog, MoodSleepAdapter.MoodViewHolder>(MoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MoodViewHolder(private val binding: ItemMoodLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(log: MoodSleepLog) {
            binding.tvDate.text = log.date
            binding.tvEmoji.text = log.moodEmoji
            binding.tvSleepInfo.text = "${log.sleepHours} hrs"
            binding.chipQuality.text = when(log.sleepQuality) {
                1 -> "Poor"
                2 -> "Okay"
                3 -> "Good"
                else -> ""
            }
        }
    }

    class MoodDiffCallback : DiffUtil.ItemCallback<MoodSleepLog>() {
        override fun areItemsTheSame(oldItem: MoodSleepLog, newItem: MoodSleepLog): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MoodSleepLog, newItem: MoodSleepLog): Boolean = oldItem == newItem
    }
}
