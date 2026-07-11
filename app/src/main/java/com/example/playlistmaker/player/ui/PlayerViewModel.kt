package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.util.toFormattedMinutesSeconds

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val url: String
) : ViewModel() {
    private val playerStateLiveData = MutableLiveData<PlayerState>(
        PlayerState(
            PlayerStatus.DEFAULT,
            0L.toFormattedMinutesSeconds()
        )
    )
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val timerRunnable = Runnable {
        if (playerStateLiveData.value?.status == PlayerStatus.PLAYING) {
            startTimerUpdate()
        }
    }

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mainThreadHandler.removeCallbacksAndMessages(null)
        playerInteractor.releasePlayer()
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value?.status) {
            PlayerStatus.PLAYING -> pausePlayer()
            PlayerStatus.PREPARED, PlayerStatus.PAUSED -> startPlayer()
            else -> { }
        }
    }

    fun onPause() {
        if (playerStateLiveData.value?.status == PlayerStatus.PLAYING) {
            pausePlayer()
        }
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer(
            url,
            onPrepared = {
                playerStateLiveData.postValue(
                    PlayerState(
                        PlayerStatus.PREPARED,
                        0L.toFormattedMinutesSeconds()
                    )
                )
            },
            onCompletion = {
                playerStateLiveData.postValue(
                    PlayerState(
                        PlayerStatus.PREPARED,
                        0L.toFormattedMinutesSeconds()
                    )
                )
                pauseTimer()
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        val currentTime = playerStateLiveData.value?.progressTime ?: 0L.toFormattedMinutesSeconds()
        playerStateLiveData.postValue(
            PlayerState(
                PlayerStatus.PLAYING,
                currentTime
            )
        )
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        playerInteractor.pausePlayer()
        val currentTime = playerStateLiveData.value?.progressTime ?: 0L.toFormattedMinutesSeconds()
        playerStateLiveData.postValue(
            PlayerState(
                PlayerStatus.PAUSED,
                currentTime
            )
        )
    }

    private fun startTimerUpdate() {
        val currentPosition = playerInteractor.getCurrentPosition()
        playerStateLiveData.postValue(
            PlayerState(
                PlayerStatus.PLAYING,
                currentPosition.toFormattedMinutesSeconds()
            )
        )
        mainThreadHandler.postDelayed(timerRunnable, DELAY)
    }

    private fun pauseTimer() {
        mainThreadHandler.removeCallbacks(timerRunnable)
    }

    companion object {
        const val DELAY = 200L
    }
}