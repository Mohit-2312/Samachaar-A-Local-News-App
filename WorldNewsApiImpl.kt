package com.example.localnewstracker.worldnewsapi

import io.ktor.client.HttpClient
import io.ktor.client.request.get

class WorldNewsApiImpl(
    private val client: HttpClient
): WorldNewsApi {
    override suspend fun getByCountry(country: String): List<WorldNewsApiArticle> {
        val response: WorldNewsApiResponse = client.get(WorldNewsApiRoutes.SEARCH_NEWS) {
                url {
                    parameters.append("source-countries", country)
                }
        }
        return response.news
    }

    override suspend fun getByQuery(query: String, country: String): List<WorldNewsApiArticle> {
        val response: WorldNewsApiResponse =  client.get(WorldNewsApiRoutes.SEARCH_NEWS) {
            url {
                parameters.append("country", country)
                parameters.append("entities", "LOC:$query")
            }
        }
        return response.news
    }

    override suspend fun getByLocation(
        location: String,
        country: String
    ): List<WorldNewsApiArticle> {
        val response: WorldNewsApiResponse = client.get(WorldNewsApiRoutes.SEARCH_NEWS) {
            url {
                parameters.append("location-filter", "$location,100")
            }
        }
        return response.news
    }
}