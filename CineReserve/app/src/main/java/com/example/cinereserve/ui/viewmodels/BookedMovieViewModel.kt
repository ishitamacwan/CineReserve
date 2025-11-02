package com.example.cinereserve.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinereserve.data.local.BookedMovieDao
import com.example.cinereserve.data.network.model.BookedMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookedMovieViewModel @Inject constructor(
    private val dao: BookedMovieDao
) : ViewModel() {

    private val _bookedMovies = MutableStateFlow<List<BookedMovie>>(emptyList())
    val bookedMovies: StateFlow<List<BookedMovie>> = _bookedMovies

    fun loadBookedMovies() {
        viewModelScope.launch {
            _bookedMovies.value = dao.getAllBookedMovies()
        }
    }
}
