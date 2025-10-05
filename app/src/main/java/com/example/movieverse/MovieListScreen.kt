package com.example.movieverse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// We now accept a function to handle clicks
@Composable
fun MovieListScreen(movies: List<Movie>, onMovieClick: (Movie) -> Unit) {
    LazyColumn {
        items(movies) { movie ->
            // We pass the movie and the click handler to each item
            MovieItem(movie = movie, onMovieClick = onMovieClick)
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onMovieClick: (Movie) -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            // This makes the entire row clickable
            .clickable { onMovieClick(movie) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
            contentDescription = movie.title,
            modifier = Modifier
                .width(100.dp)
                .height(150.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}