package com.example.thanksd.dashboard.data

import com.google.gson.annotations.SerializedName



// 일별 일기 기록
data class DashBoardByDateResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: DateDiariesDataList? // data가 null일 수 있으므로 Nullable로 선언합니다.
)

data class DateDiariesDataList(
    @SerializedName("diaryList") val diaryList: List<DiaryInfo>,
)

data class DiaryInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("image") val imageUrl: String,
)



// 주간 일기 기록
data class DashBoardByWeekResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: WeekDiariesData? // data가 null일 수 있으므로 Nullable로 선언합니다.
)

data class WeekDiariesData(
    @SerializedName("weekCounts") val weekCounts: DataByDay,
)

data class DataByDay(
    @SerializedName("MONDAY") val monday: Int,
    @SerializedName("TUESDAY") val tuesday: Int,
    @SerializedName("WEDNESDAY") val wednesday: Int,
    @SerializedName("THURSDAY") val thursday: Int,
    @SerializedName("FRIDAY") val friday: Int,
    @SerializedName("SATURDAY") val saturday: Int,
    @SerializedName("SUNDAY") val sunday: Int,
)

fun DataByDay.toList(): List<Int> {
    return listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
}

fun DataByDay.countSum(): Int {
    return monday + tuesday + wednesday + thursday + friday + saturday + sunday
}

// 월별 일기 기록
data class DashBoardByMonthResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: MonthDiariesData? // data가 null일 수 있으므로 Nullable로 선언합니다.
)

data class MonthDiariesData(
    @SerializedName("dateList") val dateList: List<String>,
)