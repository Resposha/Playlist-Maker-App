package com.example.playlistmaker

data class Track(
    val trackName: String, // название композиции
    val artistName: String, // имя исполнителя
    val trackTimeMillis: String, // продолжительность трека
    val artworkUrl100: String // ссылка на изображение обложки
)