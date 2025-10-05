package com.example.movieverse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MovieDetailsScreen(movieId: Int?, viewModel: MovieViewModel) {

    LaunchedEffect(Unit) {
        if (movieId != null) {
            viewModel.getMovieDetails(movieId)
        }
    }

    val movieDetail by viewModel.selectedMovie.collectAsState()

    // Get the live list of all favorite movies from the database
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()

    // Condition to check if the current movie is in the favorites list
    val isFavorite = favoriteMovies.any { it.id == movieId }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (movieDetail == null) {
            CircularProgressIndicator()
        } else {
            // Pass the favorite status and the click action to the content
            MovieDetailContent(
                movie = movieDetail!!,
                isFavorite = isFavorite,
                onToggleFavorite = {
                    if (isFavorite) {
                        viewModel.removeFavorite(movieDetail!!)
                    } else {
                        viewModel.addFavorite(movieDetail!!)
                    }
                }
            )
        }
    }
}

@Composable
fun MovieDetailContent(
    movie: MovieDetail,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))

        // NEW: Row to hold the title and the favorite button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f) // Give text all available space
            )
            // NEW: The Favorite Icon Button
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Favorite Button",
                    tint = if (isFavorite) Color.Yellow else Color.Gray,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Release Date: ${movie.releaseDate}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Rating: ${"%.1f".format(movie.voteAverage)} / 10",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (movie.overview.isNullOrEmpty()) "No summary available." else movie.overview,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}