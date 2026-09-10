package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.util.toFormattedMinutesSeconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val url: String
) : ViewModel() {
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var timerJob: Job? = null

    init {
        preparePlayer()
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
            url,
            onPrepared = {
                playerStateLiveData.postValue(PlayerState.Prepared())
            },
            onCompletion = {
                pauseTimer()
                playerStateLiveData.postValue(PlayerState.Prepared())
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.postValue(
            PlayerState.Playing(getCurrentPlayerPosition())
        )
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        pauseTimer()
        playerStateLiveData.postValue(
            PlayerState.Paused(getCurrentPlayerPosition())
        )
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(DELAY)
                playerStateLiveData.postValue(
                    PlayerState.Playing(getCurrentPlayerPosition())
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

    companion object {
        const val DELAY = 300L
    }
}