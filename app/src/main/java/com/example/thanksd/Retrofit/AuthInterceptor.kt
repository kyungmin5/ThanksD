package com.example.thanksd.Retrofit

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    // 안드로이드 내부 데이터
    private val sharedPreferences = context.getSharedPreferences("TokenData", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = sharedPreferences.getString("accessToken", "") ?: ""
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(request)
    }
}