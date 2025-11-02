package com.example.cinereserve.data.repository

import com.example.cinereserve.data.local.CartDao
import com.example.cinereserve.data.network.model.CartItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao,
) {

    suspend fun getCartItems() = cartDao.getAllCartItems()

    suspend fun addToCart(item: CartItem) = cartDao.addCartItem(item)

    suspend fun removeFromCart(item: CartItem) = cartDao.deleteCartItem(item)

}
