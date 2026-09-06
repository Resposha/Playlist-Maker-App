package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        if (url.isEmpty()) return

        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { onPrepared() }
        mediaPlayer.setOnCompletionListener { onCompletion() }
    }

    override fun startPlayer() = mediaPlayer.start()
    override fun pausePlayer() = mediaPlayer.pause()
    override fun releasePlayer() = mediaPlayer.release()

    override fun getCurrentPosition(): Long {
        return try {
            mediaPlayer.currentPosition.toLong()
        } catch (e: IllegalStateException) {
            0L
        }
    }

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying
}