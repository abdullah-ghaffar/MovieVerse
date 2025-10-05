@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.movieverse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: Int,
    val title: String,

    @SerialName("poster_path")
    val posterPath: String?,

    @SerialName("vote_average")
    val voteAverage: Double = 0.0 // Added default value
)