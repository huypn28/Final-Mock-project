package com.example.finalmockserver.di

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        return value?.split(",")
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}