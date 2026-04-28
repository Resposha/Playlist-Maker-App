package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    companion object {
        const val THEME_SETTINGS = "theme_settings"
        const val THEME_SWITCHER = "theme_switcher"
    }

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(THEME_SETTINGS, MODE_PRIVATE)
        switchTheme(sharedPrefs.getBoolean(THEME_SWITCHER, darkTheme))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}