@file:OptIn(kotlinx.coroutines.FlowPreview::class)

package com.example.movieverse

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// An enum to keep track of the current list mode
private enum class ListMode { POPULAR, SEARCH }

class MovieViewModel : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _selectedMovie = MutableStateFlow<MovieDetail?>(null)
    val selectedMovie: StateFlow<MovieDetail?> = _selectedMovie

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var currentPage = 1
    private var currentMode = ListMode.POPULAR

    init {
        loadMoreMovies() // Load the first page of popular movies
        observeSearchQuery()
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .onEach { query ->
                    // When a new search begins, reset everything
                    _movies.value = emptyList()
                    currentPage = 1
                    if (query.isEmpty()) {
                        currentMode = ListMode.POPULAR
                    } else {
                        currentMode = ListMode.SEARCH
                    }
                    loadMoreMovies()
                }
                .launchIn(viewModelScope)
        }
    }

    fun loadMoreMovies() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    // This 'when' statement checks the mode
                    when (currentMode) {
                        ListMode.POPULAR -> RetrofitInstance.api.getPopularMovies(
                            apiKey = "47916c968671c68c3917f87b5fa5256b",
                            page = currentPage
                        )
                        ListMode.SEARCH -> RetrofitInstance.api.searchMovies(
                            apiKey = "47916c968671c68c3917f87b5fa5256b",
                            searchQuery = _searchQuery.value,
                            page = currentPage
                        )
                    }
                }
                _movies.value = _movies.value + response.movies.filter { it.posterPath != null }
                currentPage++
            } catch (e: Exception) {
                Log.e("MovieViewModel", "API call failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMovieDetails(movieId: Int) {
        _selectedMovie.value = null
        viewModelScope.launch {
            try {
                val details = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getMovieDetails(
                        movieId = movieId,
                        apiKey = "47916c968671c68c3917f87b5fa5256b"
                    )
                }
                _selectedMovie.value = details
            } catch (e: Exception) {
                Log.e("MovieViewModel", "API call for details failed: ${e.message}")
            }
        }
    }
}