package com.matrusneh.app.ui.tip

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.matrusneh.app.databinding.FragmentAiTipBinding
import com.matrusneh.app.util.GeminiApiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class AIHealthTipFragment : Fragment() {

    private var _binding: FragmentAiTipBinding? = null
    private val binding get() = _binding!!

    private val topics = listOf("nutrition", "exercise", "sleep", "hygiene", "mental wellness", "baby development", "danger signs to watch")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiTipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.weekSlider.addOnChangeListener { _, value, _ ->
            binding.tvSelectedWeek.text = "Week ${value.toInt()}"
        }

        binding.btnGetTip.setOnClickListener {
            fetchTip()
        }
    }

    private fun fetchTip() {
        val week = binding.weekSlider.value.toInt()
        val language = if (binding.chipKannada.isChecked) "Kannada" else "English"
        val topic = topics.random()

        binding.shimmerView.visibility = View.VISIBLE
        binding.shimmerView.startShimmer()
        binding.tipCard.visibility = View.GONE

        lifecycleScope.launch {
            val tip = withContext(Dispatchers.IO) {
                GeminiApiHelper.fetchHealthTip(week, language, topic)
            }

            binding.shimmerView.stopShimmer()
            binding.shimmerView.visibility = View.GONE
            binding.tipCard.visibility = View.VISIBLE

            if (tip != null) {
                binding.tvTipText.text = tip
                saveTip(tip, week)
            } else {
                binding.tvTipText.text = "Unable to load tip. Check your internet connection."
            }
        }
    }

    private fun saveTip(tip: String, week: Int) {
        val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedTipsJson = sharedPref.getString("saved_tips", "[]")
        val tipsArray = JSONArray(savedTipsJson)
        val newTip = org.json.JSONObject().apply {
            put("tip", tip)
            put("week", week)
            put("date", System.currentTimeMillis())
        }
        tipsArray.put(newTip)
        sharedPref.edit().putString("saved_tips", tipsArray.toString()).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
