package com.example.localnewstracker.appsettings

import androidx.datastore.core.DataStore
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val sources: Set<NewsSource> = setOf(NewsSource.NewsApi, NewsSource.WorldNewsApi),
    val domains: String = "",
)

enum class NewsSource {
    NewsApi, WorldNewsApi
}
