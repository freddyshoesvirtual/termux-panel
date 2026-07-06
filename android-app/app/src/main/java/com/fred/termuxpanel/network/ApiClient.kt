package com.fred.termuxpanel.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

interface TermuxApi {
    @GET("/api/scripts")
    suspend fun getScripts(): ScriptsResponse

    @GET("/api/server")
    suspend fun getServerStatus(): ServerStatus

    @GET("/api/bot")
    suspend fun getBotStatus(): BotStatus

    @POST("/api/action")
    suspend fun postAction(@Body body: ActionRequest): ActionResponse
}

object ApiClient {
    // 127.0.0.1 porque Termux y esta app corren en el mismo dispositivo.
    // Si algún día mueves el backend a otro host de tu red local, cambia esto.
    private const val BASE_URL = "http://127.0.0.1:8199"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    val api: TermuxApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TermuxApi::class.java)
    }
}
