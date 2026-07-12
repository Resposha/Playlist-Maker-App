package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var settingsToolbar: MaterialToolbar
    private lateinit var settingsSwitch: SwitchMaterial
    private lateinit var shareTextView: TextView
    private lateinit var supportTextView: TextView
    private lateinit var userAgreementTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        settingsToolbar = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        settingsSwitch = findViewById<SwitchMaterial>(R.id.settings_switch)
        shareTextView = findViewById<TextView>(R.id.settings_share)
        supportTextView = findViewById<TextView>(R.id.settings_support)
        userAgreementTextView = findViewById<TextView>(R.id.settings_user_agreement)

        viewModel.observeThemeSettings().observe(this) { themeSettings ->
            settingsSwitch.setOnCheckedChangeListener(null)
            settingsSwitch.isChecked = themeSettings.isDarkThemeEnabled
            setSwitchListener()
        }

        settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        shareTextView.setOnClickListener { viewModel.shareApp() }
        supportTextView.setOnClickListener { viewModel.openSupport() }
        userAgreementTextView.setOnClickListener { viewModel.openTerms() }
    }

    private fun setSwitchListener() {
        settingsSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }
    }
}