package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.db.TrackDatabase
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val trackDatabase: TrackDatabase,
    private val movieDbConverter: TrackDbConverter
) : FavoriteTracksRepository
{
    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDatabase.trackDao().getTracks().map { tracks ->
            val reversedTracks = tracks.reversed()
            convertFromTrackEntity(reversedTracks)
        }
    }

    override suspend fun getFavoriteTrackIds(): List<String> {
        return trackDatabase.trackDao().getTrackIds()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = movieDbConverter.map(track)
        trackDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavorites(trackId: String) {
        trackDatabase.trackDao().deleteTrackById(trackId)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { movieDbConverter.map(it) }
    }
}