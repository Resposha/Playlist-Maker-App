package com.example.playlistmaker.library.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("playlist_id")
    val playlistId: Int, // id плейлиста
    val name: String, // название плейлиста
    val description: String?, // описание плейлиста
    @ColumnInfo("art_path")
    val artPath: String?, // путь к файлу изображения для обложки
    @ColumnInfo("tracks_ids")
    val trackIds: String, // список идентификаторов треков плейлиста
    @ColumnInfo("number_of_tracks")
    val numberOfTracks: Int // количество треков в плейлисте
)