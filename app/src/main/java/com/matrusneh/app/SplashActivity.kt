package com.matrusneh.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.matrusneh.app.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animations
        // Lotus
        binding.ivLotus.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(800)
                .start()
        }

// App Name
        binding.tvAppName.apply {
            alpha = 0f
            translationY = 60f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(400)
                .start()
        }

// Kannada Name
        binding.tvAppNameKannada.apply {
            alpha = 0f
            translationY = 60f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(400)
                .start()
        }

// Progress Bar
        binding.progressBar.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(1000)
                .start()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val onboardingComplete = sharedPref.getBoolean("onboarding_complete", false)

            val intent = if (onboardingComplete) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, OnboardingActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2500)
    }
}
