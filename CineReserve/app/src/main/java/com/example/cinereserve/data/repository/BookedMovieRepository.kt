package com.example.cinereserve.data.repository

import com.example.cinereserve.data.local.BookedMovieDao
import com.example.cinereserve.data.local.CartDao
import com.example.cinereserve.data.network.model.BookedMovie
import com.example.cinereserve.data.network.model.CartItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookedMovieRepository @Inject constructor(
    private val bookedMovieDao: BookedMovieDao,
    private val cartDao: CartDao
) {

    suspend fun bookMovie(item: CartItem) {
        val bookedMovie = BookedMovie(
            movieId = item.movieId,
            title = item.title,
            genre = item.genre,
            duration = item.duration,
            rating = item.rating,
            date = item.date,
            personCount = item.personCount,
            slot = item.selectedSlot,
            posterUrl = item.posterUrl
        )
        bookedMovieDao.insert(bookedMovie)

        cartDao.deleteCartItem(item)
    }
}
