package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist

class SmallPlaylistAdapter(
    private var playlists: List<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<SmallPlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallPlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item_small, parent, false)
        return SmallPlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmallPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onPlaylistClick(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updatePlaylists(updatedPlaylists: List<Playlist>) {
        playlists = updatedPlaylists
        notifyDataSetChanged()
    }
}