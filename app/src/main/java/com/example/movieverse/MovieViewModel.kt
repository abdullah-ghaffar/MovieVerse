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

class MovieViewModel : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _selectedMovie = MutableStateFlow<MovieDetail?>(null)
    val selectedMovie: StateFlow<MovieDetail?> = _selectedMovie

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        getPopularMovies()
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
                    if (query.isEmpty()) {
                        getPopularMovies()
                    } else {
                        searchMovies(query)
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun searchMovies(query: String) {
        Log.d("MovieViewModel", "Searching for query: $query")
        viewModelScope.launch {
            try {
                // THIS IS THE CHANGED PART
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.searchMovies(
                        apiKey = "47916c968671c68c3917f87b5fa5256b",
                        searchQuery = query
                    )
                }
                Log.d("MovieViewModel", "API Success: Found ${response.movies.size} movies for query '$query'")
                _movies.value = response.movies.filter { it.posterPath != null }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Search API call failed: ${e.message}")
            }
        }
    }

    private fun getPopularMovies() {
        viewModelScope.launch {
            try {
                // THIS IS THE CHANGED PART
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getPopularMovies(apiKey = "47916c968671c68c3917f87b5fa5256b")
                }
                Log.d("MovieViewModel", "API Success: Found ${response.movies.size} popular movies")
                _movies.value = response.movies.filter { it.posterPath != null }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "API call for list failed: ${e.message}")
            }
        }
    }

    fun getMovieDetails(movieId: Int) {
        _selectedMovie.value = null
        viewModelScope.launch {
            try {
                // THIS IS THE CHANGED PART
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