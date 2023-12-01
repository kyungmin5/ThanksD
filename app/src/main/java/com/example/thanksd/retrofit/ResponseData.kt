package com.example.thanksd.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class DiaryRequestBody(val image: String, val file: RequestBody)

data class MemberRequestBody(val email: String, val password: String)

