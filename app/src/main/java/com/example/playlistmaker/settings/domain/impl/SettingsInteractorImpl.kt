package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun isDarkThemeEnabled(): Boolean = repository.isDarkThemeEnabled()
    override fun switchTheme(isEnabled: Boolean) = repository.saveThemeSetting(isEnabled)
}