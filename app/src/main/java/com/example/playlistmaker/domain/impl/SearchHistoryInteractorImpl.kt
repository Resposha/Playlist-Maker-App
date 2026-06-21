package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun getHistory(): List<Track> = repository.getHistory()
    override fun addTrack(newTrack: Track) = repository.addTrack(newTrack)
    override fun clearHistory() = repository.clearHistory()
}