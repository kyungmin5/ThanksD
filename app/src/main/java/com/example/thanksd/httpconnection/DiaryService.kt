package com.example.thanksd.httpconnection

import androidx.lifecycle.ViewModel
import org.json.JSONObject

class DiaryService {
    private val diaryHttpService = DiaryHttpService()

    suspend fun getDiaryDates(year: Int, month: Int, viewModel: JsonViewModel): List<String>? {
        return diaryHttpService.getDiaryDates(year, month, viewModel)
    }
}
