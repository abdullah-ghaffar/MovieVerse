@file:OptIn(kotlinx.coroutines.FlowPreview::class)

package com.example.movieverse

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class ListMode { POPULAR, SEARCH }

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    // Get the DAO from the Application class we created
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

    // NEW: A flow that automatically holds the list of all favorite movies from the database
    val favoriteMovies: StateFlow<List<FavoriteMovie>> = movieDao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadMoreMovies()
        observeSearchQuery()
    }

    // NEW: Function to add a movie to favorites
    fun addFavorite(movie: MovieDetail) {
        viewModelScope.launch {
            val favoriteMovie = FavoriteMovie(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath
            )
            withContext(Dispatchers.IO) {
                movieDao.addFavorite(favoriteMovie)
            }
        }
    }

    // NEW: Function to remove a movie from favorites
    fun removeFavorite(movie: MovieDetail) {
        viewModelScope.launch {
            val favoriteMovie = FavoriteMovie(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath
            )
            withContext(Dispatchers.IO) {
                movieDao.removeFavorite(favoriteMovie)
            }
        }
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
                    _movies.value = emptyList()
                    currentPage = 1
                    currentMode = if (query.isEmpty()) ListMode.POPULAR else ListMode.SEARCH
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