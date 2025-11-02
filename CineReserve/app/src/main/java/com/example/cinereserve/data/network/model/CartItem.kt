package com.example.cinereserve.data.network.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val movieId: Int,
    val title: String,
    val posterUrl: String,
    val genre: String,
    val rating: Double,
    val duration: String,
    val cinema: String,
    var date: String,
    val time: String,
    var personCount: Int,
    val addedAt: Long = System.currentTimeMillis(),
    var selectedSlot: String? = null
)
