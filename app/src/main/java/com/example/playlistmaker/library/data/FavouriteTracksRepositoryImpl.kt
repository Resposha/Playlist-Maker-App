package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val movieDbConvertor: TrackDbConverter
) : FavouriteTracksRepository
{
    override fun getFavouriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracks().map { tracks ->
            val reversedTracks = tracks.reversed()
            convertFromTrackEntity(reversedTracks)
        }
    }

    override suspend fun getFavouriteTracksIds(): List<String> {
        return appDatabase.trackDao().getTracksIds()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = movieDbConvertor.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        val trackEntity = movieDbConvertor.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { movieDbConvertor.map(it) }
    }
}