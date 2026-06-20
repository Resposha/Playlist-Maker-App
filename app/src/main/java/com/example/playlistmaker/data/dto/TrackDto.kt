package com.example.playlistmaker.data.dto

data class TrackDto(
    val trackId: String, // id трека
    val trackName: String, // название композиции
    val artistName: String, // имя исполнителя
    val trackTimeMillis: Long, // продолжительность трека в миллисекундах
    val artworkUrl100: String, // ссылка на изображение обложки
    val collectionName: String?, // название альбома
    val releaseDate: String?, // год релиза трека
    val primaryGenreName: String, // жанр трека
    val country: String, // страна исполнителя
    val previewUrl: String? // отрывок трека
)