package com.example.cinereserve.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cinereserve.data.network.model.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY orderIndex ASC")
    suspend fun getAllMovies(): List<Movie>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Query("DELETE FROM movies")
    suspend fun clearMovies()
}
