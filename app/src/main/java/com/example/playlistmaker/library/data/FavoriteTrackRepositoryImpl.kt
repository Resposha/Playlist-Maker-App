package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.db.FavoriteTrackDatabase
import com.example.playlistmaker.library.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.library.domain.api.FavoriteTrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTrackRepositoryImpl(
    private val favoriteTrackDatabase: FavoriteTrackDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoriteTrackRepository
{
    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackDatabase.favoriteTrackDao().getTracks().map { tracks ->
            val reversedTracks = tracks.reversed()
            convertFromTrackEntity(reversedTracks)
        }
    }

    override suspend fun getFavoriteTrackIds(): List<String> {
        return favoriteTrackDatabase.favoriteTrackDao().getTrackIds()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = trackDbConverter.mapTrackToFavoriteTrackEntity(track)
        favoriteTrackDatabase.favoriteTrackDao().insertTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavorites(trackId: String) {
        favoriteTrackDatabase.favoriteTrackDao().deleteTrackById(trackId)
    }

    private fun convertFromTrackEntity(tracks: List<FavoriteTrackEntity>): List<Track> {
        return tracks.map { trackDbConverter.mapFavoriteTrackEntityToTrack(it) }
    }
}