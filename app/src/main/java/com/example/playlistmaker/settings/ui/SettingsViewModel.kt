package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.settings.domain.models.ThemeSettings

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {
    private val themeSettingsLiveData = MutableLiveData<ThemeSettings>()
    fun observeThemeSettings(): LiveData<ThemeSettings> = themeSettingsLiveData

    init {
        themeSettingsLiveData.value = settingsInteractor.getThemeSettings()
    }

    fun switchTheme(checked: Boolean) {
        val newThemeSettings = ThemeSettings(checked)
        settingsInteractor.updateThemeSetting(newThemeSettings)
        themeSettingsLiveData.value = newThemeSettings
    }

    fun shareApp() = sharingInteractor.shareApp()
    fun openSupport() = sharingInteractor.openSupport()
    fun openTerms() = sharingInteractor.openTerms()
}