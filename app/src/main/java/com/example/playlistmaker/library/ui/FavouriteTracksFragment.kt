package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteTracksFragment : Fragment() {
    private var _binding: FragmentFavouriteTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouriteTracksViewModel by viewModel()

    private lateinit var favouriteTracksAdapter: TrackAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeFavouriteTracksState().observe(viewLifecycleOwner) {
            render(it)
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            openTrackPlayer(track)
        }

        favouriteTracksAdapter = TrackAdapter(emptyList(), onTrackClickDebounce)
        binding.libraryRecyclerviewFavouriteTracks.adapter = favouriteTracksAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: FavouriteTracksState) {
        when (state) {
            is FavouriteTracksState.Empty -> showNoFavouriteTracksMessage()
            is FavouriteTracksState.Content -> showFavouriteTracks(state.tracks)
        }
    }

    private fun showNoFavouriteTracksMessage() {
        binding.libraryRecyclerviewFavouriteTracks.isVisible = false
        binding.libraryEmptyLibrary.isVisible = true
    }

    private fun showFavouriteTracks(tracks: List<Track>) {
        binding.libraryEmptyLibrary.isVisible = false
        binding.libraryRecyclerviewFavouriteTracks.isVisible = true
        favouriteTracksAdapter.updateTracks(tracks)
    }

    private fun openTrackPlayer(track: Track) {
        try {
            findNavController().navigate(
                R.id.action_libraryFragment_to_playerFragment,
                bundleOf(TRACK to track)
            )
        } catch (e: IllegalArgumentException) {
            // empty
        }
    }

    companion object {
        private const val TRACK = "track"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}