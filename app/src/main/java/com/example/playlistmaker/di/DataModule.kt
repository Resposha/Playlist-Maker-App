package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.StorageClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TrackSearchApi
import com.example.playlistmaker.search.data.storage.SharedPrefsStorageClient
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.settings.domain.models.ThemeSettings
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ITUNES = "https://itunes.apple.com"
private const val SEARCH_HISTORY_PREFS = "search_history_prefs"
private const val SEARCH_HISTORY = "search_history"
private const val SETTINGS_PREFS = "settings_prefs"
private const val THEME_SWITCHER = "theme_switcher"

val dataModule = module {

    single<TrackSearchApi> {
        Retrofit.Builder()
            .baseUrl(ITUNES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackSearchApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<SharedPreferences>(named(SEARCH_HISTORY_PREFS)) {
        androidContext()
            .getSharedPreferences(SEARCH_HISTORY_PREFS, Context.MODE_PRIVATE)
    }

    single<SharedPreferences>(named(SETTINGS_PREFS)) {
        androidContext()
            .getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
    }

    factory {
        Gson()
    }

    factory<StorageClient<List<Track>>>(named(SEARCH_HISTORY_PREFS)) {
        val type = object : TypeToken<List<Track>>() {}.type
        SharedPrefsStorageClient(
            get(named(SEARCH_HISTORY_PREFS)),
            SEARCH_HISTORY,
            type,
            get()
        )
    }

    factory<StorageClient<ThemeSettings>>(named(SETTINGS_PREFS)) {
        SharedPrefsStorageClient(
            get(named(SETTINGS_PREFS)),
            THEME_SWITCHER,
            ThemeSettings::class.java,
            get()
        )
    }

    factory {
        MediaPlayer()
    }

    factory {
        ExternalNavigator(androidContext())
    }

}