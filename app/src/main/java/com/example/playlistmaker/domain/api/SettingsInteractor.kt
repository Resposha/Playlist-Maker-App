package com.example.playlistmaker.domain.api

interface SettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(isEnabled: Boolean)
}