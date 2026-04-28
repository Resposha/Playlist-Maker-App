package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_HISTORY = "search_history"
        const val SEARCH_QUERY = "SEARCH_QUERY"
        const val EMPTY_STRING = ""
        const val ITUNES = "https://itunes.apple.com"
    }

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

        val searchHistory = SearchHistory(getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE))

        val searchHistoryAdapter = TrackAdapter(searchHistory.get()) {
            // empty
        }

        val trackAdapter = TrackAdapter(tracks) {
            searchHistory.addTrack(it)
            searchHistoryAdapter.updateSearchHistory(searchHistory.get())
        }

        trackRecyclerView.adapter = trackAdapter
        searchHistoryRecyclerView.adapter = searchHistoryAdapter

        searchToolbar.setNavigationOnClickListener {
            finish()
        }

        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryMessage.isVisible = hasFocus && searchEditText.text.isEmpty() && searchHistory.get().isNotEmpty()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clear()
            searchHistoryAdapter.updateSearchHistory(searchHistory.get())
            searchHistoryMessage.isVisible = false
        }

        fun showFoundTracks(searchResult: List<Track>) {
            noResultsMessage.isVisible = false
            connectionIssuesMessage.isVisible = false
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
            connectionIssuesMessage.isVisible = false
            noResultsMessage.isVisible = true
        }

        fun showConnectionIssuesMessage() {
            hideSearchResult()
            noResultsMessage.isVisible = false
            connectionIssuesMessage.isVisible = true
        }

        fun searchRequest() {
            iTunesSearchService.search(searchInput).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
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
                    showConnectionIssuesMessage()
                }
            })
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
                clearButton.isVisible = !s.isNullOrEmpty()
                searchHistoryMessage.isVisible = searchEditText.hasFocus() && s.isNullOrEmpty() && searchHistory.get().isNotEmpty()
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
}