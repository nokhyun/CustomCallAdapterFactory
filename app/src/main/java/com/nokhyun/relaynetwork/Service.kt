package com.nokhyun.relaynetwork

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface Service {

    @GET("atest")
    fun fetch(@Query("size") size: Int = 10000): Call<Any>  // 옛 테스트

    @POST("atest")
    fun insert(@Body data: Any): Call<Any>  // 옛 테스트
}

object Network {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private const val receiveUrl = ""
    private const val sendUrl = ""

//    private val retrofitReceive: Retrofit = Retrofit.Builder()
//        .baseUrl(receiveUrl)
//        .client(okHttpClient)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    private val retrofitSend: Retrofit = Retrofit.Builder()
//        .baseUrl(sendUrl)
//        .client(okHttpClient)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()

//    fun <T> receiverService(api: Class<T>): T {
//        Log.e("NetworkCall", "[receiverService]")
//        return retrofitReceive.create(api)
//    }
//
//    fun <T> sendService(api: Class<T>): T {
//        Log.e("NetworkCall", "[receiverSend]")
//        return retrofitSend.create(api)
//    }
}