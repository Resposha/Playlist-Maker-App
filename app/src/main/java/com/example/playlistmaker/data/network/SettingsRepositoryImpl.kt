package com.example.playlistmaker.data.network

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsRepository
import androidx.core.content.edit

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