package com.example.playlistmaker.search.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.dpToPx
import com.example.playlistmaker.util.toFormattedMinutesSeconds

class TrackViewHolder(
    itemView: View
): RecyclerView.ViewHolder(itemView) {
    private val trackName = itemView.findViewById<TextView>(R.id.track_name)
    private val artistName = itemView.findViewById<TextView>(R.id.artist_name)
    private val trackTime = itemView.findViewById<TextView>(R.id.track_time)
    private val albumArtwork = itemView.findViewById<ImageView>(R.id.album_art)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTimeMillis.toFormattedMinutesSeconds()

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder_album_art_track_list)
            .centerCrop()
            .transform(RoundedCorners(itemView.context.dpToPx(2f)))
            .into(albumArtwork)
    }
}