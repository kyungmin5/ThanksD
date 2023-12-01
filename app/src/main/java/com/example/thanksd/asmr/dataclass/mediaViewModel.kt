package com.example.thanksd.asmr.dataclass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class mediaViewModel: ViewModel() {
    val url = MutableLiveData<mediaItem>()
}