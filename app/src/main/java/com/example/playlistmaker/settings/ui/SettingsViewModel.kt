package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.main.ui.PlaylistMakerApplication
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.settings.domain.models.ThemeSettings

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val app: PlaylistMakerApplication
) : ViewModel() {
    private val themeSettingsLiveData = MutableLiveData<ThemeSettings>()
    fun observeThemeSettings(): LiveData<ThemeSettings> = themeSettingsLiveData

    init {
        themeSettingsLiveData.value = settingsInteractor.getThemeSettings()
    }

    fun switchTheme(checked: Boolean) {
        val newSettings = ThemeSettings(checked)
        settingsInteractor.updateThemeSetting(newSettings)
        app.switchTheme(checked)
        themeSettingsLiveData.value = newSettings
    }

    fun shareApp() = sharingInteractor.shareApp()
    fun openSupport() = sharingInteractor.openSupport()
    fun openTerms() = sharingInteractor.openTerms()

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as PlaylistMakerApplication
                SettingsViewModel(
                    Creator.provideSharingInteractor(app.applicationContext),
                    Creator.provideSettingsInteractor(app.applicationContext),
                    app
                )
            }
        }
    }
}