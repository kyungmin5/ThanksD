package com.example.thanksd.mainpage.dataclass

import com.google.gson.annotations.SerializedName

data class DiaryPostImg(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: GetId?
)

data class GetId(
    @SerializedName("id") val id: String
)