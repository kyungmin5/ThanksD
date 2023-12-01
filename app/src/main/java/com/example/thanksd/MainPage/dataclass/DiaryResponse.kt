package com.example.thanksd.MainPage.dataclass

import com.google.gson.annotations.SerializedName

data class DiaryResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: DiaryData? // data가 null일 수 있으므로 Nullable로 선언합니다.
)

data class DiaryData(
    @SerializedName("diaryList") val diaryList: List<DiaryItem>?
)

data class DiaryItem(
    @SerializedName("id") val id: Int,
    @SerializedName("image") val image: String
)
