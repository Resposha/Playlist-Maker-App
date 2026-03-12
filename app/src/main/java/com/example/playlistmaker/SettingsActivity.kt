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

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val settingsToolbar = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        val shareTextView = findViewById<TextView>(R.id.settings_share)
        shareTextView.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_link))
            startActivity(shareIntent)
        }

        val supportTextView = findViewById<TextView>(R.id.settings_support)
        supportTextView.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = "mailto:".toUri()
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.contact_support_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_support_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.contact_support_message))
            startActivity(supportIntent)
        }

        val userAgreementTextView = findViewById<TextView>(R.id.settings_user_agreement)
        userAgreementTextView.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, getString(R.string.user_agreement_url).toUri())
            startActivity(userAgreementIntent)
        }
    }
}