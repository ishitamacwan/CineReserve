package com.example.cinereserve.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    @TypeConverter
    fun fromGenreList(genres: List<Int>?): String {
        return genres?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toGenreList(data: String?): List<Int> {
        return data?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
    }
}
