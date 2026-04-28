package com.example.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("trackId") val id: String, // id трека
    val trackName: String, // название композиции
    val artistName: String, // имя исполнителя
    val trackTimeMillis: String, // продолжительность трека
    val artworkUrl100: String // ссылка на изображение обложки
)