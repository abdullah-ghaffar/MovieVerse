package com.example.movieverse

sealed class Screen(val route: String) {
    object MovieList : Screen("movie_list")

    // We've changed this to include a placeholder for the movie ID
    object MovieDetail : Screen("movie_detail/{movieId}") {
        // This is a helper function to build the full route with an ID
        fun createRoute(movieId: Int) = "movie_detail/$movieId"
    }
}