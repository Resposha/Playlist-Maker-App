package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    override fun getHistory(): List<Track> = repository.getHistory()
    override fun addTrack(newTrack: Track) = repository.addTrack(newTrack)
    override fun clearHistory() = repository.clearHistory()
}