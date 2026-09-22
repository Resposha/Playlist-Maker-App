package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavouriteTracksInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.toFormattedMinutesSeconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favouriteTracksInteractor: FavouriteTracksInteractor,
    private var track: Track
) : ViewModel() {
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var timerJob: Job? = null

    init {
        checkFavoriteStatusAndPrepare()
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
                playerStateLiveData.postValue(PlayerState.Prepared(track))
            },
            onCompletion = {
                pauseTimer()
                playerStateLiveData.postValue(PlayerState.Prepared(getCurrentTrack()))
            }
        )
    }

    private fun checkFavoriteStatusAndPrepare() {
        viewModelScope.launch {
            val favoriteTracksIds = favouriteTracksInteractor.getFavouriteTracksIds()
            val isFavourite = track.trackId in favoriteTracksIds
            track = track.copy(isFavorite = isFavourite)
            preparePlayer()
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.postValue(
            PlayerState.Playing(
                getCurrentTrack(),
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
                getCurrentTrack(),
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
                        getCurrentTrack(),
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

    private fun getCurrentTrack(): Track {
        return when (val state = playerStateLiveData.value) {
            is PlayerState.Prepared -> state.track
            is PlayerState.Playing -> state.track
            is PlayerState.Paused -> state.track
            else -> track
        }
    }

    fun onFavoriteClicked() {
        val currentState = playerStateLiveData.value ?: return
        val currentTrack = getCurrentTrack()

        viewModelScope.launch {
            val newFavoriteStatus = !currentTrack.isFavorite
            val updatedTrack = currentTrack.copy(isFavorite = newFavoriteStatus)

            if (newFavoriteStatus) {
                favouriteTracksInteractor.addTrackToFavorites(updatedTrack)
            } else {
                favouriteTracksInteractor.removeTrackFromFavorites(updatedTrack)
            }

            val newState = when (currentState) {
                is PlayerState.Prepared -> PlayerState.Prepared(updatedTrack)
                is PlayerState.Playing -> PlayerState.Playing(updatedTrack, currentState.progress)
                is PlayerState.Paused -> PlayerState.Paused(updatedTrack, currentState.progress)
                else -> currentState
            }

            playerStateLiveData.value = newState
        }
    }

    companion object {
        const val DELAY = 300L
    }
}