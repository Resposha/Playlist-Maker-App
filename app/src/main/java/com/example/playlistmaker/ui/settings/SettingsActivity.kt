package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.Creator
import com.example.playlistmaker.ui.app.PlaylistMakerApplication
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsInteractor: SettingsInteractor

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

        settingsInteractor = Creator.provideSettingsInteractor(this)

        settingsToolbar = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        settingsSwitch = findViewById<SwitchMaterial>(R.id.settings_switch)
        shareTextView = findViewById<TextView>(R.id.settings_share)
        supportTextView = findViewById<TextView>(R.id.settings_support)
        userAgreementTextView = findViewById<TextView>(R.id.settings_user_agreement)

        settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        settingsSwitch.isChecked = settingsInteractor.isDarkThemeEnabled()

        settingsSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as PlaylistMakerApplication).switchTheme(checked)
            settingsInteractor.switchTheme(checked)
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
            val userAgreementIntent =
                Intent(Intent.ACTION_VIEW, getString(R.string.user_agreement_url).toUri())
            startActivity(userAgreementIntent)
        }
    }
}