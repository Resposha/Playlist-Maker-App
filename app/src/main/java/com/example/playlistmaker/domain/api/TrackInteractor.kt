package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackInteractor {
    fun searchTracks(expression: String, consumer: TrackConsumer)

    fun interface TrackConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}