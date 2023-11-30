package com.example.thanksd.utils

object Constants {
    const val TAG : String = "로그"
}

enum class RESPONSE_STATE {
    OKAY,
    FAIL
}


object API {
    const val BASE_URL : String = " http://43.202.215.181:8080/"

//    const val CLIENT_ID : String = "YS7sdqX2kuYBOifsupK1A-J2S4tkMveczqAQVOEBJMs"

    const val DIARIES : String = "diaries"
    const val GET_PRESIGNED_URL : String = "diaries/presigned"

}