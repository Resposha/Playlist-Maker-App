package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.dpToPx
import com.example.playlistmaker.util.getParcelableExtraCompat
import com.example.playlistmaker.util.toFormattedMinutesSeconds
import com.google.android.material.appbar.MaterialToolbar

class PlayerActivity : AppCompatActivity() {
    private lateinit var viewModel: PlayerViewModel

    private lateinit var playerToolbar: MaterialToolbar
    private lateinit var albumArtwork: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var addToPlaylist: ImageButton
    private lateinit var playAndPause: ImageButton
    private lateinit var addToFavourite: ImageButton
    private lateinit var playbackProgress: TextView
    private lateinit var trackTimeValue: TextView
    private lateinit var collectionName: TextView
    private lateinit var collectionNameValue: TextView
    private lateinit var releaseDate: TextView
    private lateinit var releaseDateValue: TextView
    private lateinit var primaryGenreNameValue: TextView
    private lateinit var countryValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val track = intent.getParcelableExtraCompat(TRACK, Track::class.java)
        if (track == null || track.previewUrl.isNullOrEmpty()) {
            finish()
            return
        }

        playerToolbar = findViewById<MaterialToolbar>(R.id.player_toolbar)
        albumArtwork = findViewById<ImageView>(R.id.player_album_art)
        trackName = findViewById<TextView>(R.id.player_track_name)
        artistName = findViewById<TextView>(R.id.player_artist_name)
        addToPlaylist = findViewById<ImageButton>(R.id.player_button_add_to_playlist)
        playAndPause = findViewById<ImageButton>(R.id.player_button_play_and_pause)
        addToFavourite = findViewById<ImageButton>(R.id.player_button_add_to_favourite)
        playbackProgress = findViewById<TextView>(R.id.player_playback_progress)
        trackTimeValue = findViewById<TextView>(R.id.player_track_time_value)
        collectionName = findViewById<TextView>(R.id.player_collection_name)
        collectionNameValue = findViewById<TextView>(R.id.player_collection_name_value)
        releaseDate = findViewById<TextView>(R.id.player_release_date)
        releaseDateValue = findViewById<TextView>(R.id.player_release_date_value)
        primaryGenreNameValue = findViewById<TextView>(R.id.player_primary_genre_name_value)
        countryValue = findViewById<TextView>(R.id.player_country_value)

        setTrackDetails(track)

        viewModel = ViewModelProvider(this, PlayerViewModel.getFactory(track.previewUrl))[PlayerViewModel::class.java]

        viewModel.observePlayerState().observe(this) { state ->
            changeButtonIcon(state == PlayerViewModel.STATE_PLAYING)
            enableButton(state != PlayerViewModel.STATE_DEFAULT)
        }

        viewModel.observeProgressTime().observe(this) {
            playbackProgress.text = it
        }

        playerToolbar.setNavigationOnClickListener {
            finish()
        }

        playAndPause.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun enableButton(isEnabled: Boolean) {
        playAndPause.isEnabled = isEnabled
    }

    private fun changeButtonIcon(isPlaying: Boolean) {
        val buttonIcon = if (isPlaying) R.drawable.button_pause else R.drawable.button_play
        playAndPause.setImageResource(buttonIcon)
    }

    private fun setTrackDetails(track: Track) {
        Glide.with(this)
            .load(track.getCoverArtworkUrl512())
            .placeholder(R.drawable.placeholder_album_art_player)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f)))
            .into(albumArtwork)

        trackName.text = track.trackName
        artistName.text = track.artistName
        playbackProgress.text = 0L.toFormattedMinutesSeconds()
        trackTimeValue.text = track.trackTimeMillis.toFormattedMinutesSeconds()
        primaryGenreNameValue.text = track.primaryGenreName
        countryValue.text = track.country

        if (track.collectionName != null) {
            collectionName.visibility = View.VISIBLE
            collectionNameValue.visibility = View.VISIBLE
            collectionNameValue.text = track.collectionName
        } else {
            collectionName.visibility = View.GONE
            collectionNameValue.visibility = View.GONE
        }

        if (track.releaseDate != null) {
            releaseDate.visibility = View.VISIBLE
            releaseDateValue.visibility = View.VISIBLE
            releaseDateValue.text = track.releaseDate.take(4)
        } else {
            releaseDate.visibility = View.GONE
            releaseDateValue.visibility = View.GONE
        }
    }

    companion object {
        private const val TRACK = "track"
    }
}