package com.example.movieverse

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitInstance {
    // The main address for the TMDb API version 3
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    // This builds the Retrofit object
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // This creates our usable API service from the interface
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}