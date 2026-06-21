package com.example.playlistmaker.ui.search

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
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private var searchInput = EMPTY_STRING
    private var isClickAllowed = true

    private val trackInteractor = Creator.provideTrackInteractor()
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest() }

    private val tracks = arrayListOf<Track>()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

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

        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)

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

        searchHistoryAdapter = TrackAdapter(searchHistoryInteractor.getHistory()) {
            if (clickDebounce()) {
                openTrackPlayer(it)
            }
        }

        trackAdapter = TrackAdapter(tracks) {
            if (clickDebounce()) {
                searchHistoryInteractor.addTrack(it)
                searchHistoryAdapter.updateSearchHistory(searchHistoryInteractor.getHistory())
                openTrackPlayer(it)
            }
        }

        trackRecyclerView.adapter = trackAdapter
        searchHistoryRecyclerView.adapter = searchHistoryAdapter

        searchToolbar.setNavigationOnClickListener {
            finish()
        }

        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryMessage.isVisible = hasFocus && searchEditText.text.isNullOrEmpty() && searchHistoryAdapter.itemCount > 0
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            searchHistoryAdapter.updateSearchHistory(emptyList())
            searchHistoryMessage.isVisible = false
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRequest()
                true
            }
            false
        }

        clearButton.setOnClickListener {
            searchEditText.setText(EMPTY_STRING)
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
            connectionIssuesMessage.isVisible = false
            noResultsMessage.isVisible = false
            hideSearchResult()
        }

        reloadButton.setOnClickListener {
            searchRequest()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchInput = if (!s.isNullOrEmpty()) s.toString() else EMPTY_STRING

                val isSearchFieldEmpty = searchEditText.hasFocus() && s.isNullOrEmpty()
                clearButton.isVisible = !s.isNullOrEmpty()
                searchHistoryMessage.isVisible = isSearchFieldEmpty && searchHistoryAdapter.itemCount > 0

                if (isSearchFieldEmpty) {
                    mainThreadHandler.removeCallbacks(searchRunnable)
                    hideSearchResult()
                    noResultsMessage.isVisible = false
                    connectionIssuesMessage.isVisible = false
                }

                if (!isSearchFieldEmpty) {
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchEditText.addTextChangedListener(textWatcher)
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

    private fun showFoundTracks(searchResult: List<Track>) {
        searchHistoryMessage.isVisible = false
        noResultsMessage.isVisible = false
        connectionIssuesMessage.isVisible = false
        tracks.clear()
        tracks.addAll(searchResult)
        trackAdapter.notifyDataSetChanged()
        trackRecyclerView.isVisible = true
    }

    private fun hideSearchResult() {
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
        trackRecyclerView.isVisible = false
    }

    private fun showNoResultsMessage() {
        hideSearchResult()
        searchHistoryMessage.isVisible = false
        connectionIssuesMessage.isVisible = false
        noResultsMessage.isVisible = true
    }

    private fun showConnectionIssuesMessage() {
        hideSearchResult()
        searchHistoryMessage.isVisible = false
        noResultsMessage.isVisible = false
        connectionIssuesMessage.isVisible = true
    }

    private fun searchRequest() {
        if (searchInput.isEmpty()) return

        trackRecyclerView.isVisible = false
        searchHistoryMessage.isVisible = false
        noResultsMessage.isVisible = false
        connectionIssuesMessage.isVisible = false
        progressBar.isVisible = true

        trackInteractor.searchTracks(searchInput) { foundTracks ->
            mainThreadHandler.post {
                progressBar.isVisible = false

                when {
                    foundTracks == null -> {
                        showConnectionIssuesMessage()
                    }
                    foundTracks.isEmpty() -> {
                        showNoResultsMessage()
                    }
                    else -> {
                        showFoundTracks(foundTracks)
                    }
                }
            }
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun openTrackPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
            .apply {
                putExtra(TRACK, track)
            }
        startActivity(intent)
    }

    companion object {
        private const val TRACK = "track"
        private const val SEARCH_QUERY = "SEARCH_QUERY"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val EMPTY_STRING = ""
    }
}