package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    fun getFavoriteTracks(): Flow<List<Track>>

    suspend fun getFavoriteTracksIds(): List<String>

    suspend fun addTrackToFavorites(track: Track)

    suspend fun removeTrackFromFavorites(trackId: String)
}