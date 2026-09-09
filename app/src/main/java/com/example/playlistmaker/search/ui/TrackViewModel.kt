package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class TrackViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
): ViewModel() {
    var searchInput: String = EMPTY_STRING

    private var latestSearchText: String? = null

    private val trackSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { changedText ->
        searchRequest(changedText)
    }

    private val searchStateLiveData = MutableLiveData<SearchState>()
    fun observeSearchState(): LiveData<SearchState> = searchStateLiveData

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
        }
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isEmpty()) return

        searchStateLiveData.value = SearchState.Loading

        viewModelScope.launch {
            trackInteractor
                .searchTracks(newSearchText)
                .collect { foundTracks ->
                    when {
                        foundTracks == null -> {
                            searchStateLiveData.value = SearchState.ConnectionIssues
                        }
                        foundTracks.isEmpty() -> {
                            searchStateLiveData.value = SearchState.NoResults
                        }
                        else -> {
                            searchStateLiveData.value = SearchState.Content(foundTracks)
                        }
                    }
                }

        }
    }

    fun showHistory() {
        viewModelScope.launch {
            searchStateLiveData.value = SearchState.History(
                searchHistoryInteractor.getHistory()
            )
        }
    }

    fun addTrackToHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryInteractor.addTrack(track)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.clearHistory()
            searchStateLiveData.value = SearchState.History(emptyList())
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val EMPTY_STRING = ""
    }
}