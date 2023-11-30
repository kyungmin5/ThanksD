package com.example.thanksd.httpconnection

import org.json.JSONObject

class DiaryHttpService {
    private val httpFunc = HttpFunc("http://43.202.215.181:8080/diaries/calendar")

    suspend fun getDiaryDates(year: Int, month: Int, viewModel: JsonViewModel): List<String>? {
        val params = JSONObject().apply {
            put("year", year)
            put("month", month)
        }

        val response = httpFunc.POST(params, viewModel)

        return response?.let {
            val dataObject = JSONObject("data")
            val dateList = dataObject?.optJSONArray("dateList")
            val dates = mutableListOf<String>()

            dateList?.let { jsonArray ->
                for (i in 0 until jsonArray.length()) {
                    dates.add(jsonArray.optString(i))
                }
            }
            dates
        }
    }
}