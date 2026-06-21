package com.example.playlistmaker.domain.models

import android.os.Parcel
import android.os.Parcelable

data class Track(
    val trackId: String, // id трека
    val trackName: String, // название композиции
    val artistName: String, // имя исполнителя
    val trackTimeMillis: Long, // продолжительность трека
    val artworkUrl100: String, // ссылка на изображение обложки
    val collectionName: String?, // название альбома
    val releaseDate: String?, // год релиза трека
    val primaryGenreName: String, // жанр трека
    val country: String, // страна исполнителя
    val previewUrl: String? // отрывок трека
) : Parcelable {
    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(trackId)
        dest.writeString(trackName)
        dest.writeString(artistName)
        dest.writeLong(trackTimeMillis)
        dest.writeString(artworkUrl100)
        dest.writeString(collectionName)
        dest.writeString(releaseDate)
        dest.writeString(primaryGenreName)
        dest.writeString(country)
        dest.writeString(previewUrl)
    }

    fun getCoverArtworkUrl512() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(
                trackId = parcel.readString().orEmpty(),
                trackName = parcel.readString().orEmpty(),
                artistName = parcel.readString().orEmpty(),
                trackTimeMillis = parcel.readLong(),
                artworkUrl100 = parcel.readString().orEmpty(),
                collectionName = parcel.readString(),
                releaseDate = parcel.readString(),
                primaryGenreName = parcel.readString().orEmpty(),
                country = parcel.readString().orEmpty(),
                previewUrl = parcel.readString()
            )
        }
        override fun newArray(size: Int): Array<Track?> = arrayOfNulls(size)
    }
}