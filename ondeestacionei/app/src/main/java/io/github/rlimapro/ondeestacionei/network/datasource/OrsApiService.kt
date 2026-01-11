package io.github.rlimapro.ondeestacionei.network.datasource

import io.github.rlimapro.ondeestacionei.network.model.OrsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OrsApiService {
    @GET("v2/directions/{profile}")
    suspend fun getRoute(
        @Path("profile") profile: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): OrsResponse
}