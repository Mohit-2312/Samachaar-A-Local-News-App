package com.example.localnewstracker

data class NewsItem(
    val source: Int,
    val title: String,
    val description: String? = null,
    val url: String,
    val urlToImage: String? = null,
)
