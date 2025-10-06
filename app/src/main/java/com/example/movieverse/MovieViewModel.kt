@file:OptIn(kotlinx.coroutines.FlowPreview::class)

package com.example.movieverse

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class ListMode { POPULAR, SEARCH }

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val movieDao = (application as MovieApplication).database.movieDao()

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

    val favoriteMovies: StateFlow<List<FavoriteMovie>> = movieDao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchMovies(isNewSearch = true)
        observeSearchQuery()
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .drop(1) // Ignore the initial empty state at startup
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    currentMode = if (query.isEmpty()) ListMode.POPULAR else ListMode.SEARCH
                    fetchMovies(isNewSearch = true) // Start a new search
                }
        }
    }

    fun loadMoreMovies() {
        if (!_isLoading.value) { // Only load more if not already loading
            fetchMovies(isNewSearch = false)
        }
    }

    private fun fetchMovies(isNewSearch: Boolean) {
        if (isNewSearch) {
            currentPage = 1
            _movies.value = emptyList()
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
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
                val filteredMovies = response.movies.filter { it.posterPath != null }
                _movies.value = if (isNewSearch) filteredMovies else _movies.value + filteredMovies
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

    fun addFavorite(movie: MovieDetail) {
        viewModelScope.launch {
            val favoriteMovie = FavoriteMovie(id = movie.id, title = movie.title, posterPath = movie.posterPath)
            withContext(Dispatchers.IO) { movieDao.addFavorite(favoriteMovie) }
        }
    }

    fun removeFavorite(movie: MovieDetail) {
        viewModelScope.launch {
            val favoriteMovie = FavoriteMovie(id = movie.id, title = movie.title, posterPath = movie.posterPath)
            withContext(Dispatchers.IO) { movieDao.removeFavorite(favoriteMovie) }
        }
    }
}