package com.example.localnewstracker.worldnewsapi

import com.example.localnewstracker.worldnewsapi.WorldNewsApiArticle
import kotlinx.serialization.Serializable

@Serializable
data class WorldNewsApiResponse(
    val offset: Int,
    val number: Int,
    val available: Int,
    val news: List<WorldNewsApiArticle>,
)
