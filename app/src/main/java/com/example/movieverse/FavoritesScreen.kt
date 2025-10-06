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

@Composable
fun FavoritesScreen(
    favoriteMovies: List<FavoriteMovie>,
    onMovieClick: (Int) -> Unit // Click handler sends the movie ID
) {
    // Condition: If the list is empty, show a message.
    if (favoriteMovies.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "You have no favorite movies yet.")
        }
    } else {
        // Otherwise, show the list of favorite movies.
        LazyColumn {
            items(favoriteMovies) { movie ->
                FavoriteMovieItem(movie = movie, onMovieClick = {
                    onMovieClick(movie.id)
                })
            }
        }
    }
}

@Composable
fun FavoriteMovieItem(movie: FavoriteMovie, onMovieClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onMovieClick() },
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