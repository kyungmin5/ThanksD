package com.example.thanksd.httpconnection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class JsonViewModel:ViewModel() {
    val response = MutableLiveData<JSONObject>()
}