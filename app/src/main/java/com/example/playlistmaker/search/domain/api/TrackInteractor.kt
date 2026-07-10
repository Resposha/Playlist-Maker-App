package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TrackInteractor {
    fun searchTracks(expression: String, consumer: TrackConsumer)

    fun interface TrackConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}