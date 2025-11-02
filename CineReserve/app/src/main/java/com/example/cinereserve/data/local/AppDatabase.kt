package com.example.cinereserve.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cinereserve.data.network.model.CartItem

@Database(entities = [CartItem::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao


}
