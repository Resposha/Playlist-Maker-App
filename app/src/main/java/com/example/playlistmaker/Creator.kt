package com.example.playlistmaker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.data.network.PlayerRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.SettingsRepositoryImpl
import com.example.playlistmaker.data.network.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private const val THEME_SETTINGS = "theme_settings"
    private const val SEARCH_HISTORY = "search_history"

    private fun getSettingsRepository(context: Context): SettingsRepository {
        val sharedPrefs = context.getSharedPreferences(THEME_SETTINGS, Context.MODE_PRIVATE)
        return SettingsRepositoryImpl(sharedPrefs)
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
        val sharedPrefs = context.getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(sharedPrefs)
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