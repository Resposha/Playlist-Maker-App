package com.example.playlistmaker.settings.data

import com.example.playlistmaker.search.data.StorageClient
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.models.ThemeSettings

class SettingsRepositoryImpl(
    private val storage: StorageClient<ThemeSettings>
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return storage.getData() ?: ThemeSettings(false)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        storage.storeData(settings)
    }
}