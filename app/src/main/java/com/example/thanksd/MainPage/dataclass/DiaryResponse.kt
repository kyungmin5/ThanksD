package com.example.thanksd.MainPage.dataclass

data class DiaryResponse(
    val code: String,
    val message: String,
    val data: DiaryData? // data가 null일 수 있으므로 Nullable로 선언합니다.
)

data class DiaryData(
    val diaryList: List<DiaryItem>?
)

data class DiaryItem(
    val id: Int,
    val image: String
)
