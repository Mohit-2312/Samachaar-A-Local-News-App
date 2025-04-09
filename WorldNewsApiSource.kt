package com.example.localnewstracker.worldnewsapi

import kotlinx.serialization.Serializable

@Serializable()
data class WorldNewsApiSource(
    val id: String,
    val name: String,
)
