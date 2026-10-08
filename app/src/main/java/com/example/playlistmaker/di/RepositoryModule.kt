package com.example.playlistmaker.di

import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.library.data.PlaylistRepositoryImpl
import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.data.network.TrackRepositoryImpl
import com.example.playlistmaker.search.data.storage.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val SEARCH_HISTORY_PREFS = "search_history_prefs"
private const val SETTINGS_PREFS = "settings_prefs"

val repositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            get(named(SEARCH_HISTORY_PREFS)),
            get()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named(SETTINGS_PREFS)))
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    factory { TrackDbConverter() }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    factory { PlaylistDbConverter() }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

}