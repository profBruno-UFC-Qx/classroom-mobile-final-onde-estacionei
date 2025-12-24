package io.github.rlimapro.ondeestacionei.network.datasource

import io.github.rlimapro.ondeestacionei.network.model.OrsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OrsApiService {
    @GET("v2/directions/foot-walking")
    suspend fun getRoute(
        @Query("api_key") apiKey: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): OrsResponse
}