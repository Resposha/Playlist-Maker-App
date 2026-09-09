package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    suspend fun getHistory(): List<Track>
    suspend fun addTrack(newTrack: Track)
    suspend fun clearHistory()
}