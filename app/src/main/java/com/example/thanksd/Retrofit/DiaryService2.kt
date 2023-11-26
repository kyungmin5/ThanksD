package com.example.thanksd.Retrofit

import com.example.thanksd.MainPage.dataclass.DiaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DiaryService2 {
    @GET("/diaries/date")
    suspend fun getDiariesByDate(@Query("date") date: String): DiaryResponse
}