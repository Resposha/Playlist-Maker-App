package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun isDarkThemeEnabled(): Boolean
    fun saveThemeSetting(isEnabled: Boolean)
}