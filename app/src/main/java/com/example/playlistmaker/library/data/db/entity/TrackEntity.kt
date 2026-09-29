package com.example.playlistmaker.library.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey @ColumnInfo("track_id")
    val trackId: String, // id трека
    @ColumnInfo("track_name")
    val trackName: String, // название композиции
    @ColumnInfo("artist_name")
    val artistName: String, // имя исполнителя
    @ColumnInfo("track_time")
    val trackTimeMillis: Long, // продолжительность трека
    @ColumnInfo("artwork_url")
    val artworkUrl100: String, // ссылка на изображение обложки
    @ColumnInfo("collection_name")
    val collectionName: String?, // название альбома
    @ColumnInfo("release_date")
    val releaseDate: String?, // год релиза трека
    @ColumnInfo("genre")
    val primaryGenreName: String, // жанр трека
    @ColumnInfo("country")
    val country: String, // страна исполнителя
    @ColumnInfo("preview_url")
    val previewUrl: String? // отрывок трека
)