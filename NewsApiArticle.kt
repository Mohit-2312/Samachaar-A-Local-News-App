package com.example.localnewstracker.newsapi

import com.example.localnewstracker.NewsItem
import kotlinx.serialization.Serializable

@Serializable
data class NewsApiArticle(
    val title: String,
    val description: String? = null,
    val url: String,
    val urlToImage: String? = null,
) {
    fun toNewsItem() = NewsItem(0,title, description, url, urlToImage)
}