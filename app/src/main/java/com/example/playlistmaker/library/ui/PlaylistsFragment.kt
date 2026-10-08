package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    private lateinit var playlistsAdapter: BigPlaylistAdapter
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observePlaylistsState().observe(viewLifecycleOwner) { playlists ->
            if (playlists.isEmpty()) {
                binding.libraryRecyclerviewPlaylists.isVisible = false
                binding.libraryNoPlaylists.isVisible = true
            } else {
                binding.libraryNoPlaylists.isVisible = false
                binding.libraryRecyclerviewPlaylists.isVisible = true
                playlistsAdapter.updatePlaylists(playlists)
            }
        }

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            // проверить и дописать
        }

        playlistsAdapter = BigPlaylistAdapter(emptyList(), onPlaylistClickDebounce)
        binding.libraryRecyclerviewPlaylists.adapter = playlistsAdapter

        binding.libraryButtonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.showPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}