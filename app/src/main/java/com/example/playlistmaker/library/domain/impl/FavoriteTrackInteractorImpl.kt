package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker.library.domain.api.FavoriteTrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
) : FavoriteTrackInteractor {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackRepository.getFavoriteTracks()
    }

    override suspend fun getFavoriteTrackIds(): List<String> {
        return favoriteTrackRepository.getFavoriteTrackIds()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        favoriteTrackRepository.addTrackToFavorites(track)
    }

    override suspend fun removeTrackFromFavorites(trackId: String) {
        favoriteTrackRepository.removeTrackFromFavorites(trackId)
    }
}