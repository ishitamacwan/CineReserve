package com.example.cinereserve.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinereserve.data.network.model.Movie
import com.example.cinereserve.data.network.model.MovieDetailResponse
import com.example.cinereserve.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _selectedMovie = MutableLiveData<MovieDetailResponse>()
    val selectedMovie: LiveData<MovieDetailResponse> = _selectedMovie

    private val _currentSort = MutableLiveData("default")
    val currentSort: LiveData<String> = _currentSort

    private val _originalMovies = mutableListOf<Movie>()
    private var currentPage = 1
    private var isFetching = false


    fun fetchMovies(token: String, reset: Boolean = false) {
        if (isFetching) return
        if (reset) currentPage = 1

        isFetching = true
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val movieList = repository.getPopularMovies(token, page = currentPage)

                withContext(Dispatchers.Main) {
                    if (movieList.isNotEmpty()) {
                        if (reset || currentPage == 1) {
                            _originalMovies.clear()
                            _originalMovies.addAll(movieList)
                            _movies.value = movieList
                        } else {
                            _originalMovies.addAll(movieList)
                            val updatedList = _movies.value.orEmpty() + movieList
                            _movies.value = updatedList.distinctBy { it.id }
                        }
                        currentPage++
                    } else {
                        Log.e("MovieViewModel", "No movies fetched (empty or cached empty).")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("MovieViewModel", "Exception: ${e.message}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    isFetching = false
                }
            }
        }
    }


    fun sortMoviesBy(criteria: String) {
        _currentSort.value = criteria

        val sortedList = when (criteria) {
            "rating" -> _movies.value?.sortedByDescending { it.vote_average }
            "date" -> _movies.value?.sortedByDescending { it.release_date }
            "title" -> _movies.value?.sortedBy { it.title }
            "default" -> _originalMovies.toList()
            else -> _movies.value
        }

        _movies.value = sortedList ?: emptyList()
    }


    fun fetchMovieDetails(movieId: Int, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getMovieDetails(movieId, token)
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    _selectedMovie.value = response.body()
                }
            } else {
                Log.e("MovieViewModel", "Error fetching movie details: ${response.code()}")
            }
        }
    }
}
