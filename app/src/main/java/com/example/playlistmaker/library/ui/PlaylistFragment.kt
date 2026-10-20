package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

        trackAdapter = TrackAdapter(emptyList(), onTrackClickDebounce)

        playlistAdapter = SmallPlaylistAdapter(emptyList()) {
            // empty
        }

        binding.playlistRecyclerviewTracks.adapter = trackAdapter
        binding.playlistRecyclerviewMenu.adapter = playlistAdapter

        viewModel.observePlaylistState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Content -> {
                    setPlaylistDetails(state.playlist, state.tracks)

                    binding.playlistContainer.doOnLayout {
                        val totalHeight = binding.playlistContainer.height
                        val contentHeight = binding.playlistInfoContainer.height

                        val tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetTracks)
                        tracksBottomSheetBehavior.peekHeight = totalHeight - contentHeight
                    }
                }
            }
        }

        val menuBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.playlistOverlay.isVisible = false
                    }
                    else -> {
                        binding.playlistOverlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.playlistOverlay.alpha = (slideOffset + 1f) / 2f
            }
        })

        binding.playlistToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.playlistButtonMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.playlistOverlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.showPlaylist(playlistId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setPlaylistDetails(playlist: Playlist, tracks: List<Track>) {
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

        if (playlist.description != null) {
            with(binding.playlistDetailsDescription) {
                isVisible = true
                text = playlist.description
            }
        } else {
            binding.playlistDetailsDescription.isVisible = false
        }

        trackAdapter.updateTracks(tracks)
    }

    private fun openTrackPlayer(track: Track) {
        try {
            findNavController().navigate(
                R.id.action_playlistFragment_to_playerFragment,
                bundleOf(TRACK to track)
            )
        } catch (e: IllegalArgumentException) {
            // empty
        }
    }

    companion object {
        private const val TRACK = "track"
        private const val PLAYLIST_ID = "playlist_id"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}