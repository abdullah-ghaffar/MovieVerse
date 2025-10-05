@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.movieverse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    @SerialName("results")
    val movies: List<Movie>
)