package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun getHistory(consumer: HistoryConsumer)
    fun addTrack(newTrack: Track)
    fun clearHistory()

    fun interface HistoryConsumer {
        fun consume(searchHistory: List<Track>)
    }
}