package com.example.playlistmaker.library.ui

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed interface PlaylistState {
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>
    ) : PlaylistState

    object Deleted : PlaylistState
}