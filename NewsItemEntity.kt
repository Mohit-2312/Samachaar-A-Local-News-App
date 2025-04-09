package com.example.localnewstracker.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.localnewstracker.NewsItem

@Entity(tableName = "news_items", indices = [Index(value = ["title"], unique = true)])
data class NewsItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val locType: String,
    val source: Int,
    val title: String,
    val description: String? = null,
    val url: String,
    val urlToImage: String? = null,
) {
    companion object {
        fun fromNewsItem(locType: String, newsItem: NewsItem) = NewsItemEntity(
            locType=locType,
            source=newsItem.source,
            title=newsItem.title,
            description = newsItem.description,
            url = newsItem.url,
            urlToImage = newsItem.urlToImage
        )
    }
    fun getNewsItem() = NewsItem(this.source, this.title, this.description, this.url, this.urlToImage)
}
