package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConverter {
    fun mapTrackToFavoriteTrackEntity(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    fun mapFavoriteTrackEntityToTrack(favoriteTrackEntity: FavoriteTrackEntity): Track {
        return Track(
            favoriteTrackEntity.trackId,
            favoriteTrackEntity.trackName,
            favoriteTrackEntity.artistName,
            favoriteTrackEntity.trackTimeMillis,
            favoriteTrackEntity.artworkUrl100,
            favoriteTrackEntity.collectionName,
            favoriteTrackEntity.releaseDate,
            favoriteTrackEntity.primaryGenreName,
            favoriteTrackEntity.country,
            favoriteTrackEntity.previewUrl
        )
    }

    fun mapTrackToPlaylistTrackEntity(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}