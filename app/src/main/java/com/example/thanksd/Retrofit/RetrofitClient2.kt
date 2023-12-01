package com.example.thanksd.retrofit

import android.util.Log
import com.example.thanksd.login.dataclass.ClientInformation.token
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.thanksd.utils.Constants.TAG
import com.example.thanksd.utils.isJsonArray
import com.example.thanksd.utils.isJsonObject
import okhttp3.Interceptor
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

// 싱글턴
object RetrofitClient2 {

    // 레트로핏 클라이언트 선언
    private var retrofitClient: Retrofit? = null
    private var retrofitS3Client: Retrofit? = null

    // 레트로핏 클라이언트 가져오기
    fun getClient(baseUrl: String): Retrofit? {
        Log.d(TAG, "RetrofitClient - getClient() called")

        // okhttp 인스턴스 생성
        val client = OkHttpClient.Builder()

        // 로그를 찍기 위해 로깅 인터셉터 설정
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            when {
                message.isJsonObject() ->
                    Log.d("json object 로그", JSONObject(message).toString(4))

                message.isJsonArray() ->
                    Log.d("json array 로그", JSONObject(message).toString(4))

                else -> {
                    try {
                        Log.d("json 로그", JSONObject(message).toString(4))
                    } catch (e: Exception) {
                        Log.d("기타", message)
                    }
                }
            }
        }

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        // 위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(loggingInterceptor)


        // 로그인 토큰 추가 인터셉터 설정
        val baseParameterInterceptor : Interceptor = (Interceptor { chain ->
            Log.d(TAG, "RetrofitClient - intercept() called")

            // 오리지날 리퀘스트
            val originalRequest = chain.request()

            val finalRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .method(originalRequest.method, originalRequest.body)
                .build()

            chain.proceed(finalRequest)
        })


        // 위에서 설정한 기본 파라미터 인터 셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(baseParameterInterceptor)

        // 커넥션 타임 아웃
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)


        if(retrofitClient == null){

            // 레트로핏 빌더를 통해 인스턴스 생성
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                // 위에서 설정한 클라이언트로 레트로핏 클라이언트를 설정한다.
                .client(client.build())
                .build()
        }

        return retrofitClient
    }


    fun getClientForS3(): Retrofit? {
        Log.d(TAG, "RetrofitClientS3 - getClient() called")

        // okhttp 인스턴스 생성
        val client = OkHttpClient.Builder()

        // 로그를 찍기 위해 로깅 인터셉터 설정
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            when {
                message.isJsonObject() ->
                    Log.d("json object 로그", JSONObject(message).toString(4))

                message.isJsonArray() ->
                    Log.d("json array 로그", JSONObject(message).toString(4))

                else -> {
                    try {
                        Log.d("json 로그", JSONObject(message).toString(4))
                    } catch (e: Exception) {
                        Log.d("기타", message)
                    }
                }
            }
        }

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        // 위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(loggingInterceptor)


        // 커넥션 타임 아웃
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)


        if(retrofitS3Client == null){

            // 레트로핏 빌더를 통해 인스턴스 생성
            retrofitS3Client = Retrofit.Builder()
                .baseUrl("https://thanksd-image-bucket.s3.ap-northeast-2.amazonaws.com")
                .addConverterFactory(nullOnEmptyConverterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                // 위에서 설정한 클라이언트로 레트로핏 클라이언트를 설정한다.
                .client(client.build())
                .build()
        }

        return retrofitS3Client
    }

    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
            override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }
    }


}