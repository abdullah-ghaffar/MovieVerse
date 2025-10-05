package com.example.movieverse

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(movie: FavoriteMovie)

    @Delete
    suspend fun removeFavorite(movie: FavoriteMovie)

    @Query("SELECT * FROM favorite_movies")
    fun getAllFavorites(): Flow<List<FavoriteMovie>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId)")
    fun isFavorite(movieId: Int): Flow<Boolean>
}
