package com.example.playlistmaker.domain.api

interface SettingsRepository {
    fun isDarkThemeEnabled(): Boolean
    fun saveThemeSetting(isEnabled: Boolean)
}