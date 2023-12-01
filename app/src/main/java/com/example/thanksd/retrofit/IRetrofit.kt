package com.example.thanksd.retrofit

import com.example.thanksd.utils.API
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

interface IRetrofit {


    // Diaries 일기 관련 요청
    @POST(API.DIARIES)
    fun postDiary(@Body requestBody: DiaryRequestBody) : Call<JsonElement>

    @GET(API.GET_PRESIGNED_URL)
    fun getPresignedUrl(@Query("image") imageName: String) : Call<JsonElement>



}