package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.library.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getFavoriteTracks()
    }

    override suspend fun getFavoriteTracksIds(): List<String> {
        return favoriteTracksRepository.getFavoriteTracksIds()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        favoriteTracksRepository.addTrackToFavorites(track)
    }

    override suspend fun removeTrackFromFavorites(trackId: String) {
        favoriteTracksRepository.removeTrackFromFavorites(trackId)
    }
}