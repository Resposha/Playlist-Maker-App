package com.example.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist

class BigPlaylistAdapter(
    private var playlists: List<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<BigPlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BigPlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item_big, parent, false)
        return BigPlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: BigPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onPlaylistClick(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updatePlaylists(changedPlaylists: List<Playlist>) {
        playlists = changedPlaylists
        notifyDataSetChanged()
    }
}