package com.example.playlistmaker.player.ui

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val progress: String
) {
    class Default : PlayerState(false, "00:00")

    class Prepared(val isFavorite: Boolean) : PlayerState(true, "00:00")

    class Playing(val isFavorite: Boolean, progress: String) : PlayerState(true, progress)

    class Paused(val isFavorite: Boolean, progress: String) : PlayerState(true, progress)
}