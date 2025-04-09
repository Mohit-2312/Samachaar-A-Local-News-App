package com.example.localnewstracker.newsapi

import io.ktor.client.HttpClient
import io.ktor.client.request.get

class NewsApiImpl(
    private val client: HttpClient
): NewsApi {
    override suspend fun getByCountry(country: String): List<NewsApiArticle> {
        val response: NewsApiResponse = client.get(NewsApiRoutes.TOP_HEADLINES) {
                url {
                    parameters.append("country", country)
                }
        }
        return response.articles
    }
    override suspend fun getByQuery(query: String, domains: String): List<NewsApiArticle> {
        val response: NewsApiResponse =  client.get(NewsApiRoutes.EVERYTHING) {
            url {
                parameters.append("q", query)
                if (domains.isNotEmpty()) {
                    parameters.append("domains", domains)
                }
            }
        }
        return response.articles
    }
}