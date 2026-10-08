package com.example.playlistmaker.library.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.util.dpToPx

class BigPlaylistViewHolder(
    itemView: View
): RecyclerView.ViewHolder(itemView) {

    private val playlistArt = itemView.findViewById<ImageView>(R.id.playlist_art)
    private val playlistName = itemView.findViewById<TextView>(R.id.playlist_name)
    private val numberOfTracks = itemView.findViewById<TextView>(R.id.number_of_tracks)

    fun bind(model: Playlist) {
        val tracksCountText = itemView.context.resources.getQuantityString(
            R.plurals.tracks_count,
            model.numberOfTracks,
            model.numberOfTracks
        )

        playlistName.text = model.name
        numberOfTracks.text = tracksCountText

        Glide.with(itemView)
            .load(model.artPath)
            .placeholder(R.drawable.placeholder_album_and_playlist_art)
            .centerCrop()
            .transform(RoundedCorners(itemView.context.dpToPx(8f)))
            .into(playlistArt)
    }
}