package com.example.thanksd.retrofit

import android.util.Log
import com.example.thanksd.MainPage.dataclass.DiaryResponse
import com.example.thanksd.MainPage.dataclass.DiaryResponsePresignedUrl
import com.example.thanksd.MainPage.diarydataList
import com.example.thanksd.Retrofit.RetrofitClient
import com.example.thanksd.utils.API
import com.example.thanksd.utils.Constants.TAG
import com.example.thanksd.utils.RESPONSE_STATE
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitManager {
    companion object {
        val instance = RetrofitManager()
    }

    // 레트로핏 인터페이스 가져오기
    private val iRetrofit : IRetrofit? = RetrofitClient2.getClient(API.BASE_URL)?.create(IRetrofit::class.java)


    // 사진 검색 api 호출
//    fun searchPhotos(searchTerm: String?, completion: (RESPONSE_STATE, String) -> Unit){
//
//        val term = searchTerm ?: ""
//        val call = iRetrofit?.searchPhotos(searchTerm = term) ?: return
//
//        call.enqueue(object : retrofit2.Callback<JsonElement>{
//            // 응답 실패시
//            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
//                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
//
//                completion(RESPONSE_STATE.FAIL, t.toString())
//            }
//
//            // 응답 성공시
//            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
//                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")
//
//                completion(RESPONSE_STATE.OKAY ,response.body().toString())
//
//            }
//
//        })
//    }

    // 일기 저장하기
    fun postDiary(requestBody: DiaryRequestBody, completion: (RESPONSE_STATE, String) -> Unit){
        val call = iRetrofit?.postDiary(requestBody=requestBody) ?: return
        // Presigned URL 받아 오기
        instance.getPresignedUrl(
            image = requestBody.image,
            completion = { responseState, responseBody ->
                when (responseState) {
                    RESPONSE_STATE.OKAY -> {
                        Log.d(TAG, "getPresignedUrl 호출 성공 : $responseBody")
                        // Parse JSON string to JsonObject
                        val jsonObject: JsonObject =
                            JsonParser.parseString(responseBody).asJsonObject
                        val presignedUrl =
                            jsonObject.getAsJsonObject("data").get("url").asString

                        // s3에 이미지 업로드
                        instance.uploadImagetoS3(
                            preSignedUrl = presignedUrl,
                            file = requestBody.file,
                            completion = { responseState, responseBody ->
                                when (responseState) {
                                    RESPONSE_STATE.OKAY -> {
                                        Log.d(TAG, "uploadImagetoS3 호출 성공 : $responseBody")
                                        // Parse JSON string to JsonObject
//                                    val jsonObject: JsonObject =
//                                        JsonParser.parseString(responseBody).asJsonObject
//                                    presignedUrl = jsonObject.getAsJsonObject("data").get("url").asString

                                    }

                                    RESPONSE_STATE.FAIL -> {
                                        Log.d(TAG, "uploadImagetoS3 호출 실패 : $responseBody")
                                    }
                                }
                            })

                    }
                    RESPONSE_STATE.FAIL -> {
                        Log.d(TAG, "getPresignedUrl 호출 실패 : $responseBody")
                    }
                }
            })

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "postDiary RetrofitManager - onFailure() called / t: $t")
                completion(RESPONSE_STATE.FAIL, t.toString())
            }

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "postDiary RetrofitManager - onResponse() called / response : ${response.body()}")
                completion(RESPONSE_STATE.OKAY, response.body().toString())
            }
        })
    }

     //Pre-Signed Url 받아 오기
    fun getPresignedUrl(image: String?, completion: (RESPONSE_STATE, String) -> Unit){
        val imageName = image ?: ""
        val call = iRetrofit?.getPresignedUrl(imageName = imageName) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "getPresignedUrl RetrofitManager - onFailure() called / t: $t")
                completion(RESPONSE_STATE.FAIL, t.toString())
            }

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "getPresignedUrl RetrofitManager - onResponse() called / response : ${response.body()}")
                completion(RESPONSE_STATE.OKAY, response.body().toString())
            }
        })
    }

//    fun getPresignedUrl(url: String){
//        RetrofitClient.create(context).getPresignedUrl(url).enqueue(object :
//            Callback<DiaryResponsePresignedUrl> {
//            override fun onResponse(
//                call: Call<DiaryResponse>,
//                response: Response<DiaryResponse>
//            ) {
//                if(response.isSuccessful){
//                    diarydataList.clear()
//
//                    val responseBody = response.body()
//                    responseBody?.data?.diaryList?.let { newList ->
//
//                        Log.d("SUCCESS", "Setting: newList = $newList")
//
//                        diarydataList.addAll(newList)
//                    }
//                }
//                else{       // 받아오는 것을 실패했을 때-> 로그인 실패, 서버 오류
//                    Log.e("404 error", response.code().toString())
//                    Log.e("404 error", response.errorBody().toString())
//                }
//            }
//
//            override fun onFailure(call: Call<DiaryResponse>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }

    // 이미지
    fun uploadImagetoS3(preSignedUrl: String?, file: RequestBody, completion: (RESPONSE_STATE, String) -> Unit){

        val preSignedUrl = preSignedUrl ?: ""
        // 레트로핏 인터페이스 가져오기
        val iRetrofitForS3 : IRetrofitForS3? = RetrofitClient2.getClientForS3()?.create(IRetrofitForS3::class.java)
        val call =  iRetrofitForS3?.uploadImageToPresignedUrl(preSignedUrl=preSignedUrl, file=file) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "uploadImagetoS3 RetrofitManager - onFailure() called / t: $t")
                completion(RESPONSE_STATE.FAIL, t.toString())
            }

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "uploadImagetoS3 RetrofitManager - onResponse() called / response : ${response.body()}")
                completion(RESPONSE_STATE.OKAY, response.body().toString())
            }
        })
    }




}