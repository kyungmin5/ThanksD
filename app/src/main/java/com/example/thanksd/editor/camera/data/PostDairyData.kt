package com.example.thanksd.editor.camera.data

import com.google.gson.annotations.SerializedName

data class DiarySaveResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: DiarySavedId? // data가 null일 수 있으므로 Nullable로 선언합니다.
)

data class DiarySavedId(
    @SerializedName("id") val id: Int
)