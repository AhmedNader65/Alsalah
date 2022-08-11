package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.room.entity.azkar.Azkar

interface AzkarRepository :BaseRepository{
    suspend fun insertProgress(date: String, category: String)
    suspend fun getTotalProgress(date: String): Int
    suspend fun getAzkar()
    suspend fun getFirstAzkarByCategory(category: String?=null): Azkar
    suspend fun getAzkarByCategory(category: String): List<Azkar>
    suspend fun getRandomAzkar(category: String): Azkar
}