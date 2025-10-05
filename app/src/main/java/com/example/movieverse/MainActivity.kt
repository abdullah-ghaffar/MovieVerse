package com.example.movieverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movieverse.ui.theme.MovieVerseTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieVerseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val movies by viewModel.movies.collectAsState()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.MovieList.route
                    ) {
                        composable(route = Screen.MovieList.route) {
                            MovieListScreen(
                                movies = movies,
                                onMovieClick = { movie ->
                                    navController.navigate(Screen.MovieDetail.createRoute(movie.id))
                                }
                            )
                        }

                        composable(
                            route = Screen.MovieDetail.route,
                            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val movieId = backStackEntry.arguments?.getInt("movieId")
                            // We now pass the viewModel to the screen
                            MovieDetailsScreen(movieId = movieId, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}