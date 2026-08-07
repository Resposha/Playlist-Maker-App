package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track

class TrackViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
): ViewModel() {
    var searchInput: String = EMPTY_STRING

    private var latestSearchText: String? = null

    private val searchStateLiveData = MutableLiveData<SearchState>()
    fun observeSearchState(): LiveData<SearchState> = searchStateLiveData

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        val text = latestSearchText
        if (!text.isNullOrEmpty()) {
            searchRequest(text)
        }
    }

    fun searchDebounce(changedText: String) {
        if (changedText.isEmpty()) {
            mainThreadHandler.removeCallbacks(searchRunnable)
            return
        }

        if (latestSearchText == changedText) return
        this.latestSearchText = changedText

        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isEmpty()) return

        searchStateLiveData.value = SearchState.Loading

        trackInteractor.searchTracks(newSearchText) { foundTracks ->
            mainThreadHandler.post {
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
        mainThreadHandler.removeCallbacks(searchRunnable)
        searchHistoryInteractor.getHistory {
            searchStateLiveData.value = SearchState.History(it)
        }
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        searchStateLiveData.value = SearchState.History(emptyList())
    }

    override fun onCleared() {
        super.onCleared()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val EMPTY_STRING = ""
    }
}