package com.example.playlistmaker.library.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavouriteTracksState {
    object Empty : FavouriteTracksState

    data class Content(
        val tracks: List<Track>
    ) : FavouriteTracksState
}