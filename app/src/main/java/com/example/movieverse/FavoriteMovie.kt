package com.example.movieverse

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovie(
    @PrimaryKey
    val id: Int,
    val title: String,
    val posterPath: String?
)