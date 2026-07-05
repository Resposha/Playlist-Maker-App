package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) = repository.preparePlayer(url, onPrepared, onCompletion)
    override fun startPlayer() = repository.startPlayer()
    override fun pausePlayer() = repository.pausePlayer()
    override fun releasePlayer() = repository.releasePlayer()
    override fun getCurrentPosition(): Long = repository.getCurrentPosition()
    override fun isPlaying(): Boolean = repository.isPlaying()
}