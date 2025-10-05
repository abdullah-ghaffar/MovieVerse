package com.example.movieverse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (movieDetail == null) {
            CircularProgressIndicator()
        } else {
            MovieDetailContent(movie = movieDetail!!)
        }
    }
}

@Composable
fun MovieDetailContent(movie: MovieDetail) {
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
        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
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

        // YEH HISSA TABDEEL (CHANGED) HUA HAI
        Text(
            // Check karta hai ke overview null YA empty to nahi
            text = if (movie.overview.isNullOrEmpty()) {
                "No summary available."
            } else {
                movie.overview
            },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}