package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.toFormattedMinutesSeconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
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
                playerStateLiveData.postValue(PlayerState.Prepared(track.isFavorite))
            },
            onCompletion = {
                pauseTimer()
                playerStateLiveData.postValue(PlayerState.Prepared(track.isFavorite))
            }
        )
    }

    private fun checkFavoriteStatusAndPrepare() {
        viewModelScope.launch {
            val favoriteTrackIds = favoriteTracksInteractor.getFavoriteTrackIds()
            val isFavorite = track.trackId in favoriteTrackIds
            track = track.copy(isFavorite = isFavorite)
            preparePlayer()
        }
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
                favoriteTracksInteractor.addTrackToFavorites(track)
            } else {
                favoriteTracksInteractor.removeTrackFromFavorites(track.trackId)
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

    companion object {
        const val DELAY = 300L
    }
}