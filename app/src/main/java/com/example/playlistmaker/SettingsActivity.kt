package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.net.toUri
import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val THEME_SETTINGS = "theme_settings"
        const val THEME_SWITCHER = "theme_switcher"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val sharedPrefs = getSharedPreferences(THEME_SETTINGS, MODE_PRIVATE)

        val settingsToolbar = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        val settingsSwitch = findViewById<SwitchMaterial>(R.id.settings_switch)
        val shareTextView = findViewById<TextView>(R.id.settings_share)
        val supportTextView = findViewById<TextView>(R.id.settings_support)
        val userAgreementTextView = findViewById<TextView>(R.id.settings_user_agreement)

        settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        settingsSwitch.isChecked = sharedPrefs.getBoolean(THEME_SWITCHER, false)

        settingsSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit {
                putBoolean(THEME_SWITCHER, checked)
            }
        }

        shareTextView.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_link))
            startActivity(shareIntent)
        }

        supportTextView.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = "mailto:".toUri()
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.contact_support_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_support_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.contact_support_message))
            startActivity(supportIntent)
        }

        userAgreementTextView.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, getString(R.string.user_agreement_url).toUri())
            startActivity(userAgreementIntent)
        }
    }
}