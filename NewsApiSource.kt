package com.example.localnewstracker.newsapi

import kotlinx.serialization.Serializable

@Serializable()
data class NewsApiSource(
    val id: String,
    val name: String,
)
