package com.example.localnewstracker.worldnewsapi

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.header
import kotlinx.serialization.json.Json

interface WorldNewsApi {
    suspend fun getByCountry(country: String): List<WorldNewsApiArticle>
    suspend fun getByQuery(query: String, country: String): List<WorldNewsApiArticle>
    suspend fun getByLocation(location: String, country: String): List<WorldNewsApiArticle>

    companion object {
        fun create(): WorldNewsApi {
            return  WorldNewsApiImpl (
                client  = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(Json{
//                            coerceInputValues=true
                            ignoreUnknownKeys=true
                        })
                    }
                    defaultRequest {
                        header("X-Api-Key", "3adaecb9483b4c48a243a67f15c40e1f")
                    }
                }
            )
        }
    }
}