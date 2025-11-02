package com.example.cinereserve.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cinereserve.data.network.model.BookedMovie
import com.example.cinereserve.data.network.model.CartItem

@Database(entities = [BookedMovie::class], version = 1)
abstract class BookedMovieDB : RoomDatabase() {
    abstract fun bookedMovieDao(): BookedMovieDao
}