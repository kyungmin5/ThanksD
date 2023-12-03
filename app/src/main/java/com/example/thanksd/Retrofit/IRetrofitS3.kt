package com.example.thanksd.Retrofit

import com.google.gson.JsonElement

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

interface IRetrofitS3 {
    @PUT
    fun uploadImageToPresignedUrl(@Url preSignedUrl: String, @Body file: RequestBody): Call<JsonElement>
}