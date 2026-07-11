package com.example.playlistmaker.di

import com.example.playlistmaker.main.ui.PlaylistMakerApplication
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.TrackViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        TrackViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get(), androidApplication() as PlaylistMakerApplication)
    }

    viewModel { (previewUrl: String) ->
        PlayerViewModel(get(), previewUrl)
    }

}