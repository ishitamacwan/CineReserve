package com.example.cinereserve.ui.viewmodels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinereserve.data.network.model.CartItem
import com.example.cinereserve.data.repository.BookedMovieRepository
import com.example.cinereserve.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository,
    private val bookedMovieRepository: BookedMovieRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun loadCart() {
        viewModelScope.launch {
            _cartItems.value = repository.getCartItems()
        }
    }

    fun updateCartItem(item: CartItem) {
        viewModelScope.launch {
            repository.addToCart(item)
            loadCart()
        }
    }

    fun deleteCartItem(item: CartItem) {
        viewModelScope.launch {
            repository.removeFromCart(item)
            loadCart()
        }
    }
    fun bookMovie(item: CartItem, onComplete: () -> Unit) {
        viewModelScope.launch {
            bookedMovieRepository.bookMovie(item)
            loadCart() // Refresh cart after booking
            onComplete() // Notify UI
        }
    }
}
