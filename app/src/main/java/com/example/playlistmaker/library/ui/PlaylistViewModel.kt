package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private var playlistJob: Job? = null

    private val playlistStateLiveData = MutableLiveData<PlaylistState>()
    fun observePlaylistState(): LiveData<PlaylistState> = playlistStateLiveData

    fun showPlaylist(playlistId: Int) {
        playlistJob?.cancel()

        playlistJob = viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)

            playlistInteractor.getTracksByIds(playlist.trackIds).collect { tracks ->
                playlistStateLiveData.postValue(PlaylistState.Content(playlist, tracks))
            }
        }
    }
}