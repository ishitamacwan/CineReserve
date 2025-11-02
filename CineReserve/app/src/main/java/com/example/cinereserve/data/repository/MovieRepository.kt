package com.example.cinereserve.data.repository

import android.util.Log
import com.example.cinereserve.data.local.MovieDao
import com.example.cinereserve.data.network.TmdbService
import com.example.cinereserve.data.network.model.Movie
import com.example.cinereserve.data.network.model.MovieDetailResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val api: TmdbService,
    private val movieDao: MovieDao
) {
    private fun authHeader(token: String) = "Bearer $token"


    suspend fun getPopularMovies(token: String, page: Int = 1): List<Movie> {
        return try {
            val response = api.getPopularMovies(authHeader(token), page = page)
            if (response.isSuccessful && response.body() != null) {
                val movies = response.body()!!.results

                if (page == 1) {
                    movieDao.clearMovies()
                }

                val orderedMovies = movies.mapIndexed { index, movie ->
                    movie.copy(orderIndex = index)
                }
                movieDao.insertMovies(orderedMovies)

                movies
            } else {
                Log.e("MovieRepository", "API error: ${response.code()}")
                movieDao.getAllMovies()
            }
        } catch (e: Exception) {
            Log.e("MovieRepository", "Offline or error: ${e.message}")
            movieDao.getAllMovies()
        }
    }


    suspend fun getMovieDetails(movieId: Int, token: String): Response<MovieDetailResponse> {
        return api.getMovieDetails(movieId, authHeader(token))
    }
}
