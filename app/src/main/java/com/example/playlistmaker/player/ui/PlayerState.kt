package com.example.playlistmaker.player.ui

enum class PlayerStatus {
    DEFAULT,
    PREPARED,
    PLAYING,
    PAUSED
}

data class PlayerState(
    val status: PlayerStatus,
    val progressTime: String
)