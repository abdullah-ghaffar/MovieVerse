package com.example.movieverse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MovieListScreen(
    movies: List<Movie>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onMovieClick: (Movie) -> Unit,
    onLoadMore: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    val lazyListState = rememberLazyListState()

    // This condition becomes true when the last item is visible
    val isAtBottom by remember {
        derivedStateOf {
            val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
            // Check if we are at the end and the list is not empty
            if (lastVisibleItem == null || lazyListState.layoutInfo.totalItemsCount == 0) {
                false
            } else {
                lastVisibleItem.index == lazyListState.layoutInfo.totalItemsCount - 1
            }
        }
    }

    // This runs when isAtBottom becomes true
    LaunchedEffect(isAtBottom) {
        if (isAtBottom && !isLoading) {
            onLoadMore()
        }
    }

    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search Movies") },
            trailingIcon = {
                IconButton(onClick = onFavoritesClick) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorites")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )

        LazyColumn(state = lazyListState) {
            items(movies) { movie ->
                MovieItem(movie = movie, onMovieClick = onMovieClick)
            }

            // Show a loading indicator at the bottom of the list
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onMovieClick: (Movie) -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
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