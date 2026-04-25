package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val APP_PREFERENCES = "app_preferences"
const val THEME_SWITCHER = "theme_switcher"

class App : Application() {
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
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