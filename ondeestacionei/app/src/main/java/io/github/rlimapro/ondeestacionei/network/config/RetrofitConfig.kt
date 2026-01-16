package io.github.rlimapro.ondeestacionei.network.config

import io.github.rlimapro.ondeestacionei.BuildConfig
import io.github.rlimapro.ondeestacionei.network.datasource.OrsApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitConfig {
    private const val BASE_URL = "https://api.openrouteservice.org/"

    private val apiKeyInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val urlWithApiKey = originalRequest.url.newBuilder()
            .addQueryParameter("api_key", BuildConfig.ORS_API_KEY)
            .build()

        val requestWithApiKey = originalRequest.newBuilder()
            .url(urlWithApiKey)
            .build()

        chain.proceed(requestWithApiKey)
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val orsApiService: OrsApiService by lazy {
        retrofit.create(OrsApiService::class.java)
    }
}