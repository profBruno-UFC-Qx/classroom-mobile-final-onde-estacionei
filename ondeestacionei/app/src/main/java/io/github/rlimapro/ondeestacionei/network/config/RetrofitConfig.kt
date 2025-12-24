package io.github.rlimapro.ondeestacionei.network.config

import io.github.rlimapro.ondeestacionei.network.datasource.OrsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitConfig {
    private const val BASE_URL = "https://api.openrouteservice.org/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val orsApiService: OrsApiService by lazy {
        retrofit.create(OrsApiService::class.java)
    }
}