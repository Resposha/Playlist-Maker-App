package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val playlistsStateLiveData = MutableLiveData<List<Playlist>>()
    fun observePlaylistsState(): LiveData<List<Playlist>> = playlistsStateLiveData

    fun showPlaylists() {
        viewModelScope.launch {
            try {
                val playlists = playlistInteractor.getPlaylists()
                playlistsStateLiveData.postValue(playlists)
            } catch (e: Exception) {
                playlistsStateLiveData.postValue(emptyList())
            }
        }
    }
}