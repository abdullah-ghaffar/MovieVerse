package com.example.movieverse

sealed class Screen(val route: String) {
    object MovieList : Screen("movie_list")
    object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: Int) = "movie_detail/$movieId"
    }
    // NEW ADDRESS
    object Favorites : Screen("favorites")
}