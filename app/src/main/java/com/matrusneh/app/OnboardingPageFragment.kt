package com.matrusneh.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.matrusneh.app.databinding.FragmentOnboardingPageBinding

class OnboardingPageFragment : Fragment() {

    private var _binding: FragmentOnboardingPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val position = arguments?.getInt(ARG_POSITION) ?: 0
        
        when (position) {
            0 -> {
                binding.ivIllustration.setImageResource(R.drawable.ic_onboarding_kick)
                binding.tvTitle.text = "Track Baby Kicks"
                binding.tvSubtitle.text = "Never miss a movement"
                binding.tvSubtitleKannada.text = "ಮಗುವಿನ ಒದೆತ ಎಣಿಸಿ"
            }
            1 -> {
                binding.ivIllustration.setImageResource(R.drawable.ic_onboarding_checkup)
                binding.tvTitle.text = "Never Miss a Checkup"
                binding.tvSubtitle.text = "Stay on schedule"
                binding.tvSubtitleKannada.text = "ತಪಾಸಣೆ ಮರೆಯಬೇಡಿ"
            }
            2 -> {
                binding.ivIllustration.setImageResource(R.drawable.ic_onboarding_nutrition)
                binding.tvTitle.text = "Eat Well, Stay Strong"
                binding.tvSubtitle.text = "Balanced nutrition for two"
                binding.tvSubtitleKannada.text = "ಚೆನ್ನಾಗಿ ತಿನ್ನಿ, ಆರೋಗ್ಯವಾಗಿರಿ"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_POSITION = "position"

        fun newInstance(position: Int): OnboardingPageFragment {
            val fragment = OnboardingPageFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }
}
