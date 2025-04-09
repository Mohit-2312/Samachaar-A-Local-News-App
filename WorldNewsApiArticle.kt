package com.example.localnewstracker.worldnewsapi

import com.example.localnewstracker.NewsItem
import kotlinx.serialization.Serializable

@Serializable
data class WorldNewsApiArticle(
    val title: String,
    val summary: String? = null,
    val url: String,
    val image: String? = null,
) {
    fun toNewsItem() = NewsItem(1, title=title, description=summary, url=url, urlToImage=image)
}