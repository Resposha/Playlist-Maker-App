package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    private var isObserved = false

    private val playlistStateLiveData = MutableLiveData<PlaylistState>()
    fun observePlaylistState(): LiveData<PlaylistState> = playlistStateLiveData

    fun showPlaylist(playlistId: Int) {
        if (isObserved) return
        isObserved = true

        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId).collect { data ->
                if (data != null) {
                    playlistStateLiveData.postValue(PlaylistState.Content(data.first, data.second))
                } else {
                    playlistStateLiveData.postValue(PlaylistState.Deleted)
                }
            }
        }
    }

    fun sharePlaylist(message: String) {
        sharingInteractor.sharePlaylist(message)
    }

    fun removeTrack(trackId: String, playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(trackId, playlistId)
        }
    }

    fun deletePlaylist(playlistId: Int, trackIds: List<Int>) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId, trackIds)
        }
    }
}