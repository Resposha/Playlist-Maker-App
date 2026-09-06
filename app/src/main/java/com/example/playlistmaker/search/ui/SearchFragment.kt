package com.example.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var isClickAllowed = true

    private val viewModel: TrackViewModel by viewModel()
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.searchInput.isNotEmpty()) {
            binding.searchEditText.setText(viewModel.searchInput)
        }

        viewModel.observeSearchState().observe(viewLifecycleOwner) {
            render(it)
        }

        searchHistoryAdapter = TrackAdapter(emptyList(), onTrackClickDebounce)
        trackAdapter = TrackAdapter(emptyList(), onTrackClickDebounce)

        binding.searchRecyclerviewFoundTracks.adapter = trackAdapter
        binding.searchRecyclerviewHistory.adapter = searchHistoryAdapter


        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isNullOrEmpty()) {
                viewModel.showHistory()
            }
        }

        binding.searchButtonClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchRequest(viewModel.searchInput)
                true
            }
            false
        }

        binding.searchIconClear.setOnClickListener {
            binding.searchEditText.setText(EMPTY_STRING)
            val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchIconClear.windowToken, 0)
            viewModel.showHistory()
        }

        binding.searchButtonReload.setOnClickListener {
            viewModel.searchRequest(viewModel.searchInput)
        }

        binding.searchEditText.doOnTextChanged { s, _, _, _ ->
            viewModel.searchInput = s?.toString()?.trim() ?: EMPTY_STRING
            binding.searchIconClear.isVisible = !s.isNullOrEmpty()

            if (binding.searchEditText.hasFocus() && s.isNullOrEmpty()) {
                viewModel.showHistory()
            } else {
                viewModel.searchDebounce(viewModel.searchInput)
            }
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.addTrackToHistory(track)
            openTrackPlayer(track)
        }
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true

        if (binding.searchEditText.hasFocus() && binding.searchEditText.text.isNullOrEmpty()) {
            viewModel.showHistory()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.NoResults -> showNoResultsMessage()
            is SearchState.ConnectionIssues -> showConnectionIssuesMessage()
            is SearchState.Content -> showFoundTracks(state.foundTracks)
            is SearchState.History -> showSearchHistory(state.searchHistory)
        }
    }

    private fun showLoading() {
        binding.searchRecyclerviewFoundTracks.isVisible = false
        binding.searchHistory.isVisible = false
        binding.searchNoResults.isVisible = false
        binding.searchConnectionIssues.isVisible = false
        binding.searchProgressbar.isVisible = true
    }

    private fun showFoundTracks(searchResult: List<Track>) {
        binding.searchProgressbar.isVisible = false
        binding.searchHistory.isVisible = false
        binding.searchNoResults.isVisible = false
        binding.searchConnectionIssues.isVisible = false

        trackAdapter.updateTracks(searchResult)
        binding.searchRecyclerviewFoundTracks.isVisible = true
    }

    private fun showNoResultsMessage() {
        binding.searchProgressbar.isVisible = false
        binding.searchRecyclerviewFoundTracks.isVisible = false
        binding.searchHistory.isVisible = false
        binding.searchConnectionIssues.isVisible = false
        binding.searchNoResults.isVisible = true
    }

    private fun showConnectionIssuesMessage() {
        binding.searchProgressbar.isVisible = false
        binding.searchRecyclerviewFoundTracks.isVisible = false
        binding.searchHistory.isVisible = false
        binding.searchNoResults.isVisible = false
        binding.searchConnectionIssues.isVisible = true
    }

    private fun showSearchHistory(tracks: List<Track>) {
        binding.searchProgressbar.isVisible = false
        binding.searchRecyclerviewFoundTracks.isVisible = false
        binding.searchNoResults.isVisible = false
        binding.searchConnectionIssues.isVisible = false

        if (tracks.isNotEmpty() && binding.searchEditText.hasFocus()) {
            searchHistoryAdapter.updateTracks(tracks)
            binding.searchHistory.isVisible = true
        } else {
            binding.searchHistory.isVisible = false
        }
    }

    private fun openTrackPlayer(track: Track) {
        try {
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                bundleOf(TRACK to track)
            )
        } catch (e: IllegalArgumentException) {
            // empty
        }
    }

    companion object {
        private const val TRACK = "track"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val EMPTY_STRING = ""
    }
}