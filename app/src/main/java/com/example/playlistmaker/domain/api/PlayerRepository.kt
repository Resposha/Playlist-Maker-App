package com.example.playlistmaker.domain.api

interface PlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Long
    fun isPlaying(): Boolean
}