package com.example.localnewstracker.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsItemDao {
    @Query("select * from news_items where locType = :locType")
    suspend fun getNewsItems(locType: String): List<NewsItemEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsItems(newsItems: List<NewsItemEntity>)
}