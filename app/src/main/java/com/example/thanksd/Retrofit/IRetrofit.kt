package com.example.thanksd.Retrofit

import com.example.thanksd.dashboard.data.DashBoardByDateResponse
import com.example.thanksd.dashboard.data.DashBoardByMonthResponse
import com.example.thanksd.dashboard.data.DashBoardByWeekResponse
import com.example.thanksd.editor.camera.data.DiarySaveResponse
import com.example.thanksd.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class DiaryPostReqBody(
    val image: String
)

interface IRetrofit {
    // Diaries 일기 관련 요청
    @POST(API.DIARIES)
    fun postDiary(@Body requestBody: DiaryPostReqBody) : Call<DiarySaveResponse>

    @GET(API.GET_PRESIGNED_URL)
    fun getPresignedUrl(@Query("image") imageName: String) : Call<JsonElement>

    @GET(API.DIARIES_WEEK)
    fun getDiariesByWeek(@Query("date") date: String) : Call<DashBoardByWeekResponse>

    @GET(API.DIARIES_MONTH)
    fun getDiariesByMonth(@Query("year") year: String, @Query("month") month: String) : Call<DashBoardByMonthResponse>

    @GET(API.DIARIES_DATE)
    fun getDairyByDate(@Query("date") date: String) : Call<DashBoardByDateResponse>
}