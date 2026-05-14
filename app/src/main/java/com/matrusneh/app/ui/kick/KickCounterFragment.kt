package com.matrusneh.app.ui.kick

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.matrusneh.app.R
import com.matrusneh.app.data.db.AppDatabase
import com.matrusneh.app.data.repository.KickRepository
import com.matrusneh.app.databinding.FragmentKickCounterBinding
import java.text.SimpleDateFormat
import java.util.*

class KickCounterFragment : Fragment() {

    private var _binding: FragmentKickCounterBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: KickViewModel by viewModels {
        KickViewModelFactory(KickRepository(AppDatabase.getDatabase(requireContext()).kickDao()))
    }

    private var lastTapTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKickCounterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.btnKick.setOnClickListener {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastTapTime > 500) {
                animateButton()
                viewModel.addKick()
                lastTapTime = currentTime
            }
        }

        binding.btnResetSession.setOnClickListener {
            viewModel.resetSession()
        }

        viewModel.kicksToday.observe(viewLifecycleOwner) { kicks ->
            binding.tvKickCount.text = kicks.size.toString()
            binding.tvSessionTotal.text = kicks.size.toString()
            
            val oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000)
            val kicksThisHour = kicks.count { it.timestamp > oneHourAgo }
            binding.tvHourCount.text = kicksThisHour.toString()

            checkMovementAlert(kicks)
        }

        viewModel.sessionStartTime.observe(viewLifecycleOwner) { startTime ->
            if (startTime != null) {
                val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(startTime))
                binding.tvSessionTime.text = "Started at $timeStr"
            } else {
                binding.tvSessionTime.text = "Session not started"
            }
        }

        viewModel.weeklyKickData.observe(viewLifecycleOwner) { stats ->
            (binding.rvWeeklyStats.adapter as? KickWeeklyAdapter)?.submitList(stats)
        }
    }

    private fun animateButton() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 0.9f, 1.0f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 0.9f, 1.0f)
        ObjectAnimator.ofPropertyValuesHolder(binding.btnKick, scaleX, scaleY).apply {
            duration = 150
            start()
        }
    }

    private fun setupRecyclerView() {
        binding.rvWeeklyStats.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWeeklyStats.adapter = KickWeeklyAdapter()
    }

    private fun checkMovementAlert(kicks: List<com.matrusneh.app.data.db.entities.KickEvent>) {
        val twoHoursAgo = System.currentTimeMillis() - (2 * 60 * 60 * 1000)
        val recentKicks = kicks.filter { it.timestamp > twoHoursAgo }
        
        if (recentKicks.size < 10 && kicks.isNotEmpty()) {
            binding.cardLowMovement.visibility = View.VISIBLE
        } else {
            binding.cardLowMovement.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
