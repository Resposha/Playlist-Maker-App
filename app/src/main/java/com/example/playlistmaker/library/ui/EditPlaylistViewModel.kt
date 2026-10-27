package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : NewPlaylistViewModel(playlistInteractor) {
    private var playlistId: Int = 0
    private var oldArtPath: String? = null

    private val playlistEditedLiveData = MutableLiveData<Playlist?>()
    fun observePlaylistEdited(): LiveData<Playlist?> = playlistEditedLiveData

    fun showPlaylist(id: Int) {
        playlistId = id

        viewModelScope.launch(Dispatchers.IO) {
            val playlistWithTracks = playlistInteractor.getPlaylistById(id).firstOrNull()
            val playlist = playlistWithTracks?.first

            playlist?.let {
                oldArtPath = it.artPath
                playlistEditedLiveData.postValue(it)
            }
        }
    }

    fun editPlaylist(name: String, description: String?, artPath: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.updatePlaylist(
                Playlist(
                    playlistId,
                    name,
                    description,
                    artPath ?: oldArtPath
                )
            )

            playlistEditedLiveData.postValue(null)
        }
    }
}