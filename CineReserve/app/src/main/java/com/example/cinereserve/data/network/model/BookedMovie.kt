package com.example.cinereserve.data.network.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booked_movies")
data class BookedMovie(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val movieId: Int,
    val title: String,
    val genre: String,
    val duration: String,
    val rating: Double,
    val date: String,
    val personCount: Int,
    val slot: String?,
    val posterUrl: String?
)
