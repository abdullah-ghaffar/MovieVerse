package com.example.movieverse

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _selectedMovie = MutableStateFlow<MovieDetail?>(null)
    val selectedMovie: StateFlow<MovieDetail?> = _selectedMovie

    init {
        getPopularMovies()
    }

    private fun getPopularMovies() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPopularMovies(apiKey = "47916c968671c68c3917f87b5fa5256b")
                _movies.value = response.movies
            } catch (e: Exception) {
                Log.e("MovieViewModel", "API call for list failed: ${e.message}")
            }
        }
    }

    fun getMovieDetails(movieId: Int) {
        // THIS IS THE FIX: Reset the state to null before fetching new data.
        _selectedMovie.value = null

        viewModelScope.launch {
            try {
                val details = RetrofitInstance.api.getMovieDetails(
                    movieId = movieId,
                    apiKey = "47916c968671c68c3917f87b5fa5256b"
                )
                _selectedMovie.value = details
            } catch (e: Exception) {
                Log.e("MovieViewModel", "API call for details failed: ${e.message}")
            }
        }
    }
}