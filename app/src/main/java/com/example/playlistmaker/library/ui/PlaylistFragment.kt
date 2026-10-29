package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.ui.SmallPlaylistAdapter
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.util.debounce
import com.example.playlistmaker.util.toFormattedMinutesSeconds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()

    private val playlistId by lazy { arguments?.getInt(PLAYLIST_ID) ?: 0 }

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var playlistAdapter: SmallPlaylistAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var currentPlaylist: Playlist? = null
    private var currentTracks: List<Track> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (playlistId == 0) {
            findNavController().popBackStack()
            return
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            openTrackPlayer(track)
        }

        trackAdapter = TrackAdapter(emptyList(), onTrackClickDebounce) { track ->
            showDeleteTrackDialog(track)
        }

        playlistAdapter = SmallPlaylistAdapter(emptyList()) {
            // empty
        }

        binding.playlistRecyclerviewTracks.adapter = trackAdapter
        binding.playlistRecyclerviewMenu.adapter = playlistAdapter

        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetTracks)

        val menuBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.observePlaylistState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Content -> {
                    currentPlaylist = state.playlist
                    currentTracks = state.tracks

                    binding.playlistRecyclerviewTracks.isVisible = true
                    binding.playlistNoTracks.isVisible = false

                    setPlaylistDetails(state.playlist, state.tracks)
                    setTracksBottomSheetPeekHeight()

                    playlistAdapter.updatePlaylists(listOf(state.playlist))
                }
                is PlaylistState.Empty -> {
                    currentPlaylist = state.playlist
                    currentTracks = emptyList()

                    binding.playlistRecyclerviewTracks.isVisible = false
                    binding.playlistNoTracks.isVisible = true

                    setPlaylistDetails(state.playlist)
                    setTracksBottomSheetPeekHeight()
                }
                is PlaylistState.Deleted -> {
                    findNavController().popBackStack()
                }
            }
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (_binding == null) return

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.playlistOverlay.isVisible = false
                        tracksBottomSheetBehavior.isDraggable = true
                        binding.playlistRecyclerviewTracks.suppressLayout(false)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.playlistOverlay.isVisible = true
                        tracksBottomSheetBehavior.isDraggable = false
                        binding.playlistRecyclerviewTracks.suppressLayout(true)
                    }
                    else -> {
                        binding.playlistOverlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (_binding == null) return

                binding.playlistOverlay.alpha = (slideOffset + 1f) / 2f
            }
        })

        binding.playlistToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.playlistButtonShare.setOnClickListener {
            sharePlaylist()
        }

        binding.playlistButtonMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.playlistOverlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.playlistMenuShare.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.playlistMenuEditInfo.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.playlistOverlay.isVisible = false

            if (findNavController().currentDestination?.id == R.id.playlistFragment) {
                findNavController().navigate(
                    R.id.action_playlistFragment_to_editPlaylistFragment,
                    bundleOf(PLAYLIST_ID to playlistId)
                )
            }
        }

        binding.playlistMenuDelete.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

        viewModel.showPlaylist(playlistId)
    }

    override fun onResume() {
        super.onResume()

        val menuBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetMenu)
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.playlistOverlay.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setPlaylistDetails(playlist: Playlist, tracks: List<Track> = emptyList()) {
        Glide.with(requireContext())
            .load(playlist.artPath)
            .placeholder(R.drawable.placeholder_album_and_playlist_art)
            .transform(CenterCrop())
            .into(binding.playlistArt)

        val totalDurationRaw = tracks.sumOf { it.trackTimeMillis }.toDouble() / 60000.0
        val totalDuration = kotlin.math.ceil(totalDurationRaw).toInt()

        val minutesCount = resources.getQuantityString(
            R.plurals.minutes_count,
            totalDuration,
            totalDuration
        )

        val tracksCount = resources.getQuantityString(
            R.plurals.tracks_count,
            tracks.size,
            tracks.size
        )

        binding.playlistName.text = playlist.name
        binding.playlistDetailsTotalDuration.text = minutesCount
        binding.playlistDetailsNumberOfTracks.text = tracksCount

        if (playlist.description.isNullOrBlank()) {
            binding.playlistDetailsDescription.isVisible = false
        } else {
            with(binding.playlistDetailsDescription) {
                isVisible = true
                text = playlist.description
            }
        }

        trackAdapter.updatePlaylistTracks(tracks)
    }

    private fun setTracksBottomSheetPeekHeight() {
        binding.playlistContainer.doOnLayout {
            if (_binding == null) return@doOnLayout

            val totalHeight = binding.playlistContainer.height
            val contentHeight = binding.playlistInfoContainer.height

            tracksBottomSheetBehavior.peekHeight = totalHeight - contentHeight
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.delete_track_dialog_message)
            .setNegativeButton(R.string.dialog_negative_button_no) { _, _ ->
                // empty
            }
            .setPositiveButton(R.string.dialog_positive_button_yes) { _, _ ->
                viewModel.removeTrack(track.trackId, playlistId)
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        val playlist = currentPlaylist ?: return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_playlist_dialog_title)
            .setMessage(R.string.delete_playlist_dialog_message)
            .setNeutralButton(R.string.dialog_neutral_button_cancel) { _, _ ->
                // empty
            }
            .setPositiveButton(R.string.delete_playlist_dialog_positive_button) { _, _ ->
                viewModel.deletePlaylist(playlist.playlistId, playlist.trackIds)
            }
            .show()
    }

    private fun sharePlaylist() {
        val playlist = currentPlaylist ?: return

        if (currentTracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                R.string.no_tracks_to_share,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val tracksCount = resources.getQuantityString(
            R.plurals.tracks_count,
            currentTracks.size,
            currentTracks.size
        )

        val message = buildString {
            appendLine(playlist.name)
            if (!playlist.description.isNullOrBlank()) {
                appendLine(playlist.description)
            }
            appendLine(tracksCount)

            currentTracks.forEachIndexed { index, track ->
                val duration = track.trackTimeMillis.toFormattedMinutesSeconds()
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
            }
        }

        viewModel.sharePlaylist(message)
    }

    private fun openTrackPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_playerFragment,
            bundleOf(TRACK to track)
        )
    }

    companion object {
        private const val TRACK = "track"
        private const val PLAYLIST_ID = "playlist_id"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}