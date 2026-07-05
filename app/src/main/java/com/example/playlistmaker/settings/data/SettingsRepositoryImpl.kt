package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {
    override fun isDarkThemeEnabled(): Boolean {
        return sharedPrefs.getBoolean(THEME_SWITCHER, false)
    }

    override fun saveThemeSetting(isEnabled: Boolean) {
        sharedPrefs.edit { putBoolean(THEME_SWITCHER, isEnabled) }
    }

    companion object {
        private const val THEME_SWITCHER = "theme_switcher"
    }
}