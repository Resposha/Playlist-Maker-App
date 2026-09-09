package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override suspend fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override suspend fun addTrack(newTrack: Track) {
        repository.addTrack(newTrack)
    }

    override suspend fun clearHistory() {
        repository.clearHistory()
    }
}