package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private var searchInput = EMPTY_STRING
    private var isClickAllowed = true

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private lateinit var viewModel: TrackViewModel
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter

    private lateinit var searchToolbar: MaterialToolbar
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var noResultsMessage: LinearLayout
    private lateinit var connectionIssuesMessage: LinearLayout
    private lateinit var reloadButton: Button
    private lateinit var searchHistoryMessage: LinearLayout
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        viewModel = ViewModelProvider(this, TrackViewModel.getFactory())[TrackViewModel::class.java]

        searchToolbar = findViewById<MaterialToolbar>(R.id.search_toolbar)
        searchEditText = findViewById<EditText>(R.id.search_edit_text)
        clearButton = findViewById<ImageView>(R.id.search_icon_clear)
        trackRecyclerView = findViewById<RecyclerView>(R.id.search_recyclerview_found_tracks)
        noResultsMessage = findViewById<LinearLayout>(R.id.search_no_results)
        connectionIssuesMessage = findViewById<LinearLayout>(R.id.search_connection_issues)
        reloadButton = findViewById<Button>(R.id.search_button_reload)
        searchHistoryMessage = findViewById<LinearLayout>(R.id.search_history)
        searchHistoryRecyclerView = findViewById<RecyclerView>(R.id.search_recyclerview_history)
        clearHistoryButton = findViewById<Button>(R.id.search_button_clear_history)
        progressBar = findViewById<ProgressBar>(R.id.search_progressbar)

        viewModel.observeSearchState().observe(this) {
            render(it)
        }

        searchHistoryAdapter = TrackAdapter(emptyList()) {
            if (clickDebounce()) {
                viewModel.addTrackToHistory(it)
                openTrackPlayer(it)
            }
        }

        trackAdapter = TrackAdapter(emptyList()) {
            if (clickDebounce()) {
                viewModel.addTrackToHistory(it)
                openTrackPlayer(it)
            }
        }

        trackRecyclerView.adapter = trackAdapter
        searchHistoryRecyclerView.adapter = searchHistoryAdapter

        searchToolbar.setNavigationOnClickListener {
            finish()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isNullOrEmpty()) {
                viewModel.showHistory()
            }
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchRequest(searchInput)
                true
            }
            false
        }

        clearButton.setOnClickListener {
            searchEditText.setText(EMPTY_STRING)
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
            viewModel.showHistory()
        }

        reloadButton.setOnClickListener {
            viewModel.searchRequest(searchInput)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchInput = s?.toString()?.trim() ?: EMPTY_STRING
                clearButton.isVisible = !s.isNullOrEmpty()

                if (searchEditText.hasFocus() && s.isNullOrEmpty()) {
                    viewModel.showHistory()
                } else {
                    viewModel.searchDebounce(searchInput)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchEditText.addTextChangedListener(textWatcher)
    }

    override fun onResume() {
        super.onResume()
        if (searchEditText.hasFocus() && searchEditText.text.isNullOrEmpty()) {
            viewModel.showHistory()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInput = savedInstanceState.getString(SEARCH_QUERY, EMPTY_STRING)
        searchEditText.setText(searchInput)
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
        trackRecyclerView.isVisible = false
        searchHistoryMessage.isVisible = false
        noResultsMessage.isVisible = false
        connectionIssuesMessage.isVisible = false
        progressBar.isVisible = true
    }

    private fun showFoundTracks(searchResult: List<Track>) {
        progressBar.isVisible = false
        searchHistoryMessage.isVisible = false
        noResultsMessage.isVisible = false
        connectionIssuesMessage.isVisible = false

        trackAdapter.updateTracks(searchResult)
        trackRecyclerView.isVisible = true
    }

    private fun showNoResultsMessage() {
        progressBar.isVisible = false
        trackRecyclerView.isVisible = false
        searchHistoryMessage.isVisible = false
        connectionIssuesMessage.isVisible = false
        noResultsMessage.isVisible = true
    }

    private fun showConnectionIssuesMessage() {
        progressBar.isVisible = false
        trackRecyclerView.isVisible = false
        searchHistoryMessage.isVisible = false
        noResultsMessage.isVisible = false
        connectionIssuesMessage.isVisible = true
    }

    private fun showSearchHistory(tracks: List<Track>) {
        progressBar.isVisible = false
        trackRecyclerView.isVisible = false
        noResultsMessage.isVisible = false
        connectionIssuesMessage.isVisible = false

        if (tracks.isNotEmpty() && searchEditText.hasFocus()) {
            searchHistoryAdapter.updateTracks(tracks)
            searchHistoryMessage.isVisible = true
        } else {
            searchHistoryMessage.isVisible = false
        }
    }

    private fun openTrackPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
            .apply {
                putExtra(TRACK, track)
            }
        startActivity(intent)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val TRACK = "track"
        private const val SEARCH_QUERY = "SEARCH_QUERY"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val EMPTY_STRING = ""
    }
}