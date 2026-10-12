package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.SingleLiveEvent
import com.example.playlistmaker.util.toFormattedMinutesSeconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private var track: Track
) : ViewModel() {
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    fun observePlaylists(): LiveData<List<Playlist>> = playlistsLiveData

    private val playlistAddingStatusLiveData = SingleLiveEvent<Pair<String, Boolean>>()
    fun observePlaylistAddingStatus(): LiveData<Pair<String, Boolean>> = playlistAddingStatusLiveData

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            checkFavoriteStatus()
            checkPlaylists()
            preparePlayer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> { }
        }
    }

    fun onPause() {
        if (playerStateLiveData.value is PlayerState.Playing) {
            pausePlayer()
        }
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer(
            track.previewUrl ?: "",
            onPrepared = {
                playerStateLiveData.postValue(PlayerState.Prepared(track.isFavorite))
            },
            onCompletion = {
                pauseTimer()
                playerStateLiveData.postValue(PlayerState.Prepared(track.isFavorite))
            }
        )
    }

    private suspend fun checkFavoriteStatus() {
        val favoriteTrackIds = favoriteTrackInteractor.getFavoriteTrackIds()
        val isFavorite = track.trackId in favoriteTrackIds
        track = track.copy(isFavorite = isFavorite)
    }

    private suspend fun checkPlaylists() {
        val playlists = playlistInteractor.getPlaylists()
        playlistsLiveData.postValue(playlists)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.postValue(
            PlayerState.Playing(
                track.isFavorite,
                getCurrentPlayerPosition()
            )
        )
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        pauseTimer()
        playerStateLiveData.postValue(
            PlayerState.Paused(
                track.isFavorite,
                getCurrentPlayerPosition()
            )
        )
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(DELAY)
                playerStateLiveData.postValue(
                    PlayerState.Playing(
                        track.isFavorite,
                        getCurrentPlayerPosition()
                    )
                )
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun getCurrentPlayerPosition(): String {
        return playerInteractor.getCurrentPosition().toFormattedMinutesSeconds()
    }

    fun onFavoriteClicked() {
        val currentState = playerStateLiveData.value ?: return

        viewModelScope.launch {
            val newFavoriteStatus = !track.isFavorite
            track = track.copy(isFavorite = newFavoriteStatus)

            if (newFavoriteStatus) {
                favoriteTrackInteractor.addTrackToFavorites(track)
            } else {
                favoriteTrackInteractor.removeTrackFromFavorites(track.trackId)
            }

            val newState = when (currentState) {
                is PlayerState.Prepared -> PlayerState.Prepared(track.isFavorite)
                is PlayerState.Playing -> PlayerState.Playing(track.isFavorite, currentState.progress)
                is PlayerState.Paused -> PlayerState.Paused(track.isFavorite, currentState.progress)
                else -> currentState
            }

            playerStateLiveData.value = newState
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        val trackId = track.trackId.toIntOrNull() ?: 0

        if (playlist.trackIds.contains(trackId)) {
            playlistAddingStatusLiveData.postValue(Pair(playlist.name, false))
        } else {
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(track, playlist)
                checkPlaylists()
                playlistAddingStatusLiveData.postValue(Pair(playlist.name, true))
            }
        }
    }

    fun updatePlaylists() {
        viewModelScope.launch {
            checkPlaylists()
        }
    }

    companion object {
        const val DELAY = 300L
    }
}