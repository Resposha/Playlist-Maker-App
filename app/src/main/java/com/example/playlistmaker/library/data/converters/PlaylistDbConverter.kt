package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        val jsonString = Gson().toJson(playlist.trackIds)

        return PlaylistEntity(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            playlist.artPath,
            jsonString,
            playlist.numberOfTracks
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Int>>() {}.type

        val list: List<Int> = Gson()
            .fromJson(
                playlistEntity.trackIds,
                type
            ) ?: emptyList()

        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.name,
            playlistEntity.description,
            playlistEntity.artPath,
            list,
            playlistEntity.numberOfTracks
        )
    }
}