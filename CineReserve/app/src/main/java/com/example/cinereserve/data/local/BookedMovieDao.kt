package com.example.cinereserve.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cinereserve.data.network.model.BookedMovie

@Dao
interface BookedMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookedMovie: BookedMovie)

    @Query("SELECT * FROM booked_movies ORDER BY id DESC")
    suspend fun getAllBookedMovies(): List<BookedMovie>
}
