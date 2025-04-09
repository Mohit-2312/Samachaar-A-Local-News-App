package com.example.localnewstracker.newsapi

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.header
import kotlinx.serialization.json.Json

interface NewsApi {
    suspend fun getByCountry(country: String): List<NewsApiArticle>
    suspend fun getByQuery(query: String, domains: String): List<NewsApiArticle>

    companion object {
        fun create(): NewsApi {
            return  NewsApiImpl (
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
                        header("X-Api-Key", "11ffe12fa8f14b0e9f515568d7a61e38")
                    }
                }
            )
        }
    }
}