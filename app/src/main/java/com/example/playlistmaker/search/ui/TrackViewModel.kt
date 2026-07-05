package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.main.ui.PlaylistMakerApplication
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track


class TrackViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
): ViewModel() {
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
        val history = searchHistoryInteractor.getHistory()
        searchStateLiveData.value = SearchState.History(history)
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
        if (searchStateLiveData.value is SearchState.History) {
            showHistory()
        }
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

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as PlaylistMakerApplication)
                TrackViewModel(
                    Creator.provideTrackInteractor(),
                    Creator.provideSearchHistoryInteractor(app)
                )
            }
        }
    }
}
