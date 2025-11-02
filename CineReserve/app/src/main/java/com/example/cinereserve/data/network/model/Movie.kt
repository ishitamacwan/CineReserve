package com.example.cinereserve.data.network.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")

data class Movie(
    @PrimaryKey val id: Int,
    val title: String,
    val original_title: String,
    val overview: String,
    val poster_path: String?,
    val backdrop_path: String?,
    val genre_ids: List<Int>,
    val release_date: String?,
    val vote_average: Double,
    val vote_count: Int,
    val popularity: Double,
    val adult: Boolean,
    val video: Boolean,
    val original_language: String,
    val orderIndex: Int = 0
)