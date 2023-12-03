package com.example.thanksd.retrofit

import com.example.thanksd.mainpage.dataclass.DiaryResponse
import com.example.thanksd.mainpage.dataclass.DiaryResponseByMonth
import com.example.thanksd.mainpage.dataclass.DiaryResponsePresignedUrl
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DiaryService2 {
    @GET("/diaries/date")
    fun getDiariesByDate(@Query("date") date: String): Call<DiaryResponse>      // retrofit을 가져다 쓰기 때문에 Call도 retrofit을 씀

    @GET("/diaries/calendar")
    fun getDiriesByMonth(@Query("year") year: Int, @Query("month") month : Int): Call<DiaryResponseByMonth>

    @GET("/diaries/presigned")
    fun getPresignedUrl(@Query("image") imageName: String) : Call<DiaryResponsePresignedUrl>

//    @POST("/diaries")
//    fun postDiary(@Body() requestBody: DiaryRequestBody) : Call<DiaryPostImg>
}
