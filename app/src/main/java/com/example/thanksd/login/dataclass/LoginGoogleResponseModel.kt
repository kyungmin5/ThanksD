package com.example.thanksd.login.dataclass

import com.google.gson.annotations.SerializedName

data class LoginGoogleResponseModel(
    @SerializedName("access_token") var access_token: String
)