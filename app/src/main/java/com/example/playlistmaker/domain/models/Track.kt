package com.example.playlistmaker.domain.models

data class Track(
    val id: String, // id трека
    val trackName: String, // название композиции
    val artistName: String, // имя исполнителя
    val trackTime: String, // продолжительность трека
    val artworkUrl100: String, // ссылка на изображение обложки
    val collectionName: String?, // название альбома
    val releaseDate: String?, // год релиза трека
    val primaryGenreName: String, // жанр трека
    val country: String, // страна исполнителя
    val previewUrl: String? // отрывок трека
) {
    fun getCoverArtworkUrl512() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}