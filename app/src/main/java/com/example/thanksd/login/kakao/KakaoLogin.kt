package com.example.thanksd.login.kakao
import android.app.Application
import com.example.thanksd.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule

class KakaoLogin :Application(){
        override fun onCreate() {
            super.onCreate()
            // 다른 초기화 코드들
            startKoin {
                androidLogger()
                androidContext(this@KakaoLogin)
                modules(defaultModule)
            }
            // Kakao SDK 초기화
            val nativeKey = BuildConfig.API_KEY
            KakaoSdk.init(this, nativeKey)
        }
}