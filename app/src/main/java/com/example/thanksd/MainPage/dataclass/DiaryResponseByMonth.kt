package com.example.thanksd.mainpage.dataclass

import com.google.gson.annotations.SerializedName

data class DiaryResponseByMonth(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: DiaryDataByMonth? // data가 null일 수 있으므로 Nullable로 선언합니다.
)

data class DiaryDataByMonth(
    @SerializedName("dateList") val diaryList: List<String>?
)

