package com.example.thanksd.mainpage.dataclass

import com.google.gson.annotations.SerializedName

data class DiaryResponsePresignedUrl(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PresignedUrlByData?
)

data class PresignedUrlByData(
    @SerializedName("url") val url: String
)