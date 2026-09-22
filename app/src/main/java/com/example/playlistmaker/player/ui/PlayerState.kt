package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.domain.models.Track

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val progress: String
) {
    class Default : PlayerState(false, "00:00")

    class Prepared(val track: Track) : PlayerState(true, "00:00")

    class Playing(val track: Track, progress: String) : PlayerState(true, progress)

    class Paused(val track: Track, progress: String) : PlayerState(true, progress)
}