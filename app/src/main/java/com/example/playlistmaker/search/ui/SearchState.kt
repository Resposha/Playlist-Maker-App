package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    object Loading : SearchState

    object NoResults : SearchState

    object ConnectionIssues : SearchState

    data class Content(
        val foundTracks: List<Track>
    ) : SearchState

    data class History(
        val searchHistory: List<Track>
    ) : SearchState
}