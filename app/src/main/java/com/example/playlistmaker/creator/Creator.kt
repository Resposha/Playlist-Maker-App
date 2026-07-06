package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.storage.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.network.TrackRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.data.storage.SharedPrefsStorageClient
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.settings.domain.models.ThemeSettings
import com.google.gson.reflect.TypeToken

object Creator {
    private const val SETTINGS_PREFS = "theme_settings_prefs"
    private const val THEME_SWITCHER = "theme_switcher"
    private const val SEARCH_HISTORY_PREFS = "search_history_prefs"
    private const val SEARCH_HISTORY = "search_history"

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigator(context.applicationContext)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            context.applicationContext,
            getExternalNavigator(context)
        )
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        val storageClient = SharedPrefsStorageClient<ThemeSettings>(
            context,
            SETTINGS_PREFS,
            THEME_SWITCHER,
            ThemeSettings::class.java
        )
        return SettingsRepositoryImpl(storageClient)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val type = object : TypeToken<List<Track>>() {}.type
        val storageClient = SharedPrefsStorageClient<List<Track>>(
            context,
            SEARCH_HISTORY_PREFS,
            SEARCH_HISTORY,
            type
        )
        return SearchHistoryRepositoryImpl(storageClient)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
}