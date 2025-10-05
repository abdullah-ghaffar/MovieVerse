@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.movieverse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String?, // Changed to String?

    @SerialName("poster_path")
    val posterPath: String?, // Changed to String?

    @SerialName("release_date")
    val releaseDate: String,

    @SerialName("vote_average")
    val voteAverage: Double
)