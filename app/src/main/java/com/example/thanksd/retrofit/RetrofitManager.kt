package com.example.thanksd.retrofit

import android.util.Log
import com.example.thanksd.dashboard.data.DashBoardByDateResponse
import com.example.thanksd.dashboard.data.DashBoardByMonthResponse
import com.example.thanksd.dashboard.data.DashBoardByWeekResponse
import com.example.thanksd.editor.camera.data.DiarySaveResponse
import com.example.thanksd.utils.API
import com.example.thanksd.utils.Constants.TAG
import com.example.thanksd.utils.RESPONSE_STATE
import com.google.gson.JsonElement
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.suspendCoroutine

class RetrofitManager {
    companion object {
        val instance = RetrofitManager()
    }

    // 레트로핏 인터페이스 가져오기
    private val iRetrofit  = RetrofitClientSub.getClient(API.BASE_URL)?.create(IRetrofit::class.java)

    // 일기 저장하기
    fun postDiary(requestBody: DiaryPostReqBody, completion: (RESPONSE_STATE) -> Unit){
        val call = iRetrofit?.postDiary(requestBody =requestBody) ?: return

        call.enqueue(object : retrofit2.Callback<DiarySaveResponse> {
            // 응답 실패시
            override fun onFailure(call: Call<DiarySaveResponse>, t: Throwable) {
                Log.d(TAG, "postDiary RetrofitManager - onFailure() called / t: $t")
                completion(RESPONSE_STATE.FAIL)
            }

            // 응답 성공시
            override fun onResponse(call: Call<DiarySaveResponse>, response: Response<DiarySaveResponse>) {
                Log.d(TAG, "postDiary RetrofitManager - onResponse() called / response : ${response.body()}")
                val responseBody = response.body()
                if(responseBody?.message.equals("OK")){
                    Log.d("responseBody id", responseBody?.data?.id.toString())
                    completion(RESPONSE_STATE.OKAY)
                }else{
                    completion(RESPONSE_STATE.FAIL)
                }

            }
        })
    }

    // Pre-Signed Url 받아 오기
    suspend fun getPresignedUrl(image: String?): String{

        return suspendCoroutine { continuation ->
            val imageName = image ?: ""
            val call = iRetrofit?.getPresignedUrl(imageName = imageName) ?: return@suspendCoroutine

            call.enqueue(object : retrofit2.Callback<JsonElement> {
                // 응답 실패시
                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "이미지 presignedUrl 받기 요청 실패 onFailure / t: $t")
                    continuation.resumeWith(Result.failure(t))
                }

                // 응답 성공시
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    Log.d(TAG, "이미지 presignedUrl 받기 요청 성공 onResponse / response : ${response.body()}")
                    val result = response.body()?.toString() ?: ""
                    continuation.resumeWith(Result.success(result))
                }
            })
        }

    }

    // 이미지
    suspend fun uploadImagetoS3(preSignedUrl: String?, file: RequestBody) : RESPONSE_STATE{
        return suspendCoroutine { continuation ->
            val preSignedUrl = preSignedUrl ?: ""
            // 레트로핏 인터페이스 가져오기
            val iRetrofitS3 = RetrofitClientSub.getClientForS3()?.create(IRetrofitS3::class.java)
            val call =  iRetrofitS3?.uploadImageToPresignedUrl(preSignedUrl=preSignedUrl, file=file) ?: return@suspendCoroutine

            call.enqueue(object : retrofit2.Callback<JsonElement> {
                // 응답 실패시
                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "이미지 S3로 업로드 요청 실패 onFailure / t: $t")
                    continuation.resumeWith(Result.failure(t))
                }

                // 응답 성공시
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    Log.d(TAG, "이미지 S3 업로드 요청 성공 onResponse / response : ${response.body()}")
                    val result = response.body()?.toString() ?: "이미지 S3 업로드 요청 성공"
                    continuation.resumeWith(Result.success(RESPONSE_STATE.OKAY))
                }
            })

        }
    }

    // 주간 일기 정보 받아오기
//    fun getDiariesByWeek(date: String,  completion: (RESPONSE_STATE, String) -> Unit){
//        val call = iRetrofit?.getDiariesByWeek(date=date) ?: return
//
//        call.enqueue(object : retrofit2.Callback<DashBoardByWeekResponse> {
//            // 응답 실패시
//            override fun onFailure(call: Call<DashBoardByWeekResponse>, t: Throwable) {
//                Log.d(TAG, "postDiary RetrofitManager - onFailure() called / t: $t")
//                completion(RESPONSE_STATE.FAIL, t.toString())
//            }
//
//            // 응답 성공시
//            override fun onResponse(call: Call<DashBoardByWeekResponse>, response: Response<DashBoardByWeekResponse>) {
//                Log.d(TAG, "postDiary RetrofitManager - onResponse() called / response : ${response.body()}")
//                val responseBody = response.body()
//                if(responseBody?.message.equals("OK")){
//                    completion(RESPONSE_STATE.OKAY, responseBody?.data?.weekCounts.toString())
//                }else{
//                    completion(RESPONSE_STATE.FAIL, "FAIL TO LOAD")
//                }
//            }
//        })
//    }


    suspend fun getDiariesByWeek(date: String) : DashBoardByWeekResponse? {
        return suspendCoroutine { continuation ->
            val call = iRetrofit?.getDiariesByWeek(date=date) ?: return@suspendCoroutine

            call.enqueue(object : retrofit2.Callback<DashBoardByWeekResponse> {
                // 응답 실패시
                override fun onFailure(call: Call<DashBoardByWeekResponse>, t: Throwable) {
                    Log.d(TAG, "getDiariesByWeek RetrofitManager - onFailure() called / t: $t")
                    continuation.resumeWith(Result.failure(t))
                }

                // 응답 성공시
                override fun onResponse(call: Call<DashBoardByWeekResponse>, response: Response<DashBoardByWeekResponse>) {
                    Log.d(TAG, "getDiariesByWeek RetrofitManager - onResponse() called / response : ${response.body()}")
                    continuation.resumeWith(Result.success(response.body()))
                }
            })

        }
    }

    suspend fun getDiariesByMonth(year: String, month: String) : DashBoardByMonthResponse? {
        return suspendCoroutine { continuation ->
            val call = iRetrofit?.getDiariesByMonth(year=year, month=month) ?: return@suspendCoroutine

            call.enqueue(object : retrofit2.Callback<DashBoardByMonthResponse> {
                // 응답 실패시
                override fun onFailure(call: Call<DashBoardByMonthResponse>, t: Throwable) {
                    Log.d(TAG, "getDiariesByMonth RetrofitManager - onFailure() called / t: $t")
                    continuation.resumeWith(Result.failure(t))
                }

                // 응답 성공시
                override fun onResponse(call: Call<DashBoardByMonthResponse>, response: Response<DashBoardByMonthResponse>) {
                    Log.d(TAG, "getDiariesByMonth RetrofitManager - onResponse() called / response : ${response.body()}")
                    continuation.resumeWith(Result.success(response.body()))
                }
            })

        }
    }

    suspend fun getDiariesByDate(date: String) : DashBoardByDateResponse? {
        return suspendCoroutine { continuation ->
            val call = iRetrofit?.getDairyByDate(date=date) ?: return@suspendCoroutine

            call.enqueue(object : retrofit2.Callback<DashBoardByDateResponse> {
                // 응답 실패시
                override fun onFailure(call: Call<DashBoardByDateResponse>, t: Throwable) {
                    Log.d(TAG, "getDiariesByMonth RetrofitManager - onFailure() called / t: $t")
                    continuation.resumeWith(Result.failure(t))
                }

                // 응답 성공시
                override fun onResponse(call: Call<DashBoardByDateResponse>, response: Response<DashBoardByDateResponse>) {
                    Log.d(TAG, "getDiariesByMonth RetrofitManager - onResponse() called / response : ${response.body()}")
                    continuation.resumeWith(Result.success(response.body()))
                }
            })

        }
    }


}