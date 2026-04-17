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
import android.widget.Toast
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
    private var searchInput = INPUT

    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"
        const val INPUT = ""
    }

    private val iTunesSearchBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesSearchBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesSearchService = retrofit.create(TrackSearchApi::class.java)

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var noResultsMessage: LinearLayout
    private lateinit var connectionIssuesMessage: LinearLayout
    private lateinit var reloadButton: Button

    private val tracks = mutableListOf<Track>()
    private val trackAdapter = TrackAdapter(tracks)

//    private val trackAdapterForTests = TrackAdapter(
//        listOf(
//            Track("Smells Like Teen Spirit", "Nirvana", "5:01", "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
//            Track("Billie Jean", "Michael Jackson", "4:35", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
//            Track("Stayin' Alive", "Bee Gees", "4:10", "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
//            Track("Whole Lotta Love", "Led Zeppelin", "5:33", "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
//            Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
//        )
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val searchToolbar = findViewById<MaterialToolbar>(R.id.search_toolbar)
        searchToolbar.setNavigationOnClickListener {
            finish()
        }

        searchEditText = findViewById<EditText>(R.id.search_edit_text)
        clearButton = findViewById<ImageView>(R.id.search_icon_clear)
        trackRecyclerView = findViewById<RecyclerView>(R.id.search_track_recyclerview)
        noResultsMessage = findViewById<LinearLayout>(R.id.search_no_results)
        connectionIssuesMessage = findViewById<LinearLayout>(R.id.search_connection_issues)
        reloadButton = findViewById<Button>(R.id.search_button_reload)

        trackRecyclerView.adapter = trackAdapter

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRequest()
                true
            }
            false
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
            hideNoResultsMessage()
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
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    Toast.makeText(this@SearchActivity, "Поисковой запрос не введен.", Toast.LENGTH_SHORT).show()
                } else {
                    searchInput = s.toString()
                    Toast.makeText(this@SearchActivity, "Вы ввели следующий поисковой запрос: '$searchInput'", Toast.LENGTH_SHORT).show()
                }
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
        searchInput = savedInstanceState.getString(SEARCH_QUERY, INPUT)
        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        searchEditText.setText(searchInput)
    }

    private fun showFoundTracks(searchResult: List<Track>) {
        hideNoResultsMessage()
        hideConnectionIssuesMessage()
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
        hideConnectionIssuesMessage()
        noResultsMessage.isVisible = true
    }

    private fun hideNoResultsMessage() {
        noResultsMessage.isVisible = false
    }

    private fun showConnectionIssuesMessage() {
        hideSearchResult()
        hideNoResultsMessage()
        connectionIssuesMessage.isVisible = true
    }

    private fun hideConnectionIssuesMessage() {
        connectionIssuesMessage.isVisible = false
    }

    private fun searchRequest() {
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
}