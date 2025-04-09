package com.example.localnewstracker.newsapi

object NewsApiRoutes {
    private const val BASE_URL = "https://newsapi.org/v2"
    const val EVERYTHING = "$BASE_URL/everything"
    const val TOP_HEADLINES = "$BASE_URL/top-headlines"
}