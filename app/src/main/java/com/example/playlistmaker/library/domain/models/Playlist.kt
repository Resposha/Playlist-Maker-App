package com.example.playlistmaker.library.domain.models

data class Playlist(
    val playlistId: Int = 0, // id плейлиста
    val name: String, // название плейлиста
    val description: String?, // описание плейлиста
    val artPath: String?, // путь к файлу изображения для обложки
    val trackIds: List<Int> = emptyList(), // список идентификаторов треков плейлиста
    val numberOfTracks: Int = 0 // количество треков в плейлисте
)