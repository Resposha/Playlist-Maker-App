package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                onTrackClick(tracks[currentPosition])
            }
        }

        holder.itemView.setOnLongClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                onTrackLongClick(tracks[currentPosition])
                true
            } else {
                false
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    fun updatePlaylistTracks(newTracks: List<Track>) {
        if (this.tracks.size - newTracks.size == 1) {
            val deletedTrack = this.tracks.firstOrNull { oldTrack -> newTracks.none { newTrack -> newTrack.trackId == oldTrack.trackId } }
            if (deletedTrack != null) {
                val index = this.tracks.indexOf(deletedTrack)
                this.tracks = newTracks
                notifyItemRemoved(index)
                return
            }
        }

        tracks = newTracks
        notifyDataSetChanged()
    }
}