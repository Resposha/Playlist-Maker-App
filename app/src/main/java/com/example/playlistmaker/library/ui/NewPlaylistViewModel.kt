package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.NewPlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val newPlaylistInteractor: NewPlaylistInteractor
) : ViewModel() {
    private val playlistCreatedLiveData = SingleLiveEvent<String>()
    fun observePlaylistCreated(): LiveData<String> = playlistCreatedLiveData

    fun createPlaylist(
        name: String,
        description: String?,
        artPath: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            newPlaylistInteractor
                .createPlaylist(
                    Playlist(
                        name = name,
                        description = description,
                        artPath = artPath
                    )
                )
            playlistCreatedLiveData.postValue("Плейлист $name создан")
        }
    }
}