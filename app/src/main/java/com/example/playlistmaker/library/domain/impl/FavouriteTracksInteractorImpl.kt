package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.FavouriteTracksInteractor
import com.example.playlistmaker.library.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavouriteTracksInteractorImpl(
    private val favouriteTracksRepository: FavouriteTracksRepository
) : FavouriteTracksInteractor {

    override fun getFavouriteTracks(): Flow<List<Track>> {
        return favouriteTracksRepository.getFavouriteTracks()
    }

    override suspend fun getFavouriteTracksIds(): List<String> {
        return favouriteTracksRepository.getFavouriteTracksIds()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        favouriteTracksRepository.addTrackToFavorites(track)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        favouriteTracksRepository.removeTrackFromFavorites(track)
    }
}