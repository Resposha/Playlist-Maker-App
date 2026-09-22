package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTracksRepository {
    fun getFavouriteTracks(): Flow<List<Track>>

    suspend fun getFavouriteTracksIds(): List<String>

    suspend fun addTrackToFavorites(track: Track)

    suspend fun removeTrackFromFavorites(track: Track)
}