package com.example.playlistmaker

import android.content.Context
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
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val TRACK = "track"
        private const val SEARCH_HISTORY = "search_history"
        private const val SEARCH_QUERY = "SEARCH_QUERY"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val EMPTY_STRING = ""
        private const val ITUNES = "https://itunes.apple.com"
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

    private var searchInput = EMPTY_STRING

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesSearchService = retrofit.create(TrackSearchApi::class.java)

    private val tracks = arrayListOf<Track>()

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

        val searchHistory = SearchHistory(getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE))

        val searchHistoryAdapter = TrackAdapter(searchHistory.get()) {
            if (clickDebounce()) {
                openTrackPlayer(it)
            }
        }

        val trackAdapter = TrackAdapter(tracks) {
            if (clickDebounce()) {
                searchHistory.addTrack(it)
                searchHistoryAdapter.updateSearchHistory(searchHistory.get())
                openTrackPlayer(it)
            }
        }

        trackRecyclerView.adapter = trackAdapter
        searchHistoryRecyclerView.adapter = searchHistoryAdapter

        searchToolbar.setNavigationOnClickListener {
            finish()
        }

        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryMessage.isVisible = hasFocus && searchEditText.text.isNullOrEmpty() && searchHistory.get().isNotEmpty()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clear()
            searchHistoryAdapter.updateSearchHistory(searchHistory.get())
            searchHistoryMessage.isVisible = false
        }

        fun showFoundTracks(searchResult: List<Track>) {
            searchHistoryMessage.isVisible = false
            noResultsMessage.isVisible = false
            connectionIssuesMessage.isVisible = false
            tracks.clear()
            tracks.addAll(searchResult)
            trackAdapter.notifyDataSetChanged()
            trackRecyclerView.isVisible = true
        }

        fun hideSearchResult() {
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            trackRecyclerView.isVisible = false
        }

        fun showNoResultsMessage() {
            hideSearchResult()
            searchHistoryMessage.isVisible = false
            connectionIssuesMessage.isVisible = false
            noResultsMessage.isVisible = true
        }

        fun showConnectionIssuesMessage() {
            hideSearchResult()
            searchHistoryMessage.isVisible = false
            noResultsMessage.isVisible = false
            connectionIssuesMessage.isVisible = true
        }

        fun searchRequest() {
            progressBar.isVisible = true
            iTunesSearchService.search(searchInput).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    progressBar.isVisible = false
                    if (response.code() == 200) {
                        val searchResult = response.body()?.results.orEmpty()
                        if (searchResult.isEmpty()) {
                            showNoResultsMessage()
                        } else {
                            showFoundTracks(searchResult)
                        }
                    } else {
                        showConnectionIssuesMessage()
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    progressBar.isVisible = false
                    showConnectionIssuesMessage()
                }
            })
        }

        val searchRunnable = Runnable { searchRequest() }

        fun searchDebounce() {
            mainThreadHandler.removeCallbacks(searchRunnable)
            mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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
                searchHistoryMessage.isVisible = isSearchFieldEmpty && searchHistory.get().isNotEmpty()

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInput = savedInstanceState.getString(SEARCH_QUERY, EMPTY_STRING)
        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        searchEditText.setText(searchInput)
    }

    private fun openTrackPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
            .apply {
                putExtra(TRACK, Gson().toJson(track))
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
}