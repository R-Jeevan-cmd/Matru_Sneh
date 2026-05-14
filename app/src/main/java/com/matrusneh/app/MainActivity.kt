package com.matrusneh.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.matrusneh.app.databinding.ActivityMainBinding
import com.matrusneh.app.databinding.LayoutMoreMenuBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        
        binding.navView.setupWithNavController(navController)

        binding.navView.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_more) {
                showMoreMenu()
                false
            } else {
                navController.navigate(item.itemId)
                true
            }
        }
    }

    private fun showMoreMenu() {
        val bottomSheet = BottomSheetDialog(this)
        val moreBinding = LayoutMoreMenuBinding.inflate(layoutInflater)
        bottomSheet.setContentView(moreBinding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        moreBinding.menuVitals.setOnClickListener {
            navController.navigate(R.id.navigation_vitals)
            bottomSheet.dismiss()
        }
        moreBinding.menuMood.setOnClickListener {
            navController.navigate(R.id.navigation_mood_sleep)
            bottomSheet.dismiss()
        }
        moreBinding.menuAiTip.setOnClickListener {
            navController.navigate(R.id.navigation_ai_tip)
            bottomSheet.dismiss()
        }
        moreBinding.menuExport.setOnClickListener {
            // Trigger Export Logic (can be a dialog or navigation)
            bottomSheet.dismiss()
            showExportDialog()
        }

        bottomSheet.show()
    }

    private fun showExportDialog() {
        // Implementation for Export Dialog
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export -> {
                showExportDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
