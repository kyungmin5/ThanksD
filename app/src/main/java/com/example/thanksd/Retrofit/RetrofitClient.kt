package com.example.thanksd.Retrofit

import com.example.thanksd.httpconnection.DiaryService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://43.202.215.181:8080"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val diaryService: DiaryService2 by lazy {
        retrofit.create(DiaryService2::class.java)
    }
}