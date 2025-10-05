package com.example.movieverse

import android.app.Application

class MovieApplication : Application() {
    // Create the database instance only when it's first needed
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}