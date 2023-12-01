package com.example.thanksd.login.kakao

import android.app.Application
import android.util.Log
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.thanksd.httpconnection.HttpFunc
import com.example.thanksd.httpconnection.JsonViewModel
import com.example.thanksd.login.dataclass.ClientInformation
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class KakaoAuthViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "카카오어스"
    }
    private val context = application.applicationContext
    
    /* 될 경우 access token 전달 */
    val isLoggedIn = MutableStateFlow<String>("-1")

    fun kakaoLogin(){
        viewModelScope.launch {
            isLoggedIn.emit(kakaoLoginHandler())
        }
    }

    private suspend fun kakaoLoginHandler(): String =
        /* 토근 저장 코드 필요 (coroutine 으로 전달 (access token??))*/
        suspendCoroutine<String> { continuation ->

            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오계정으로 로그인 실패", error)
                    continuation.resume("-1")
                } else if (token != null) {
                    Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                    continuation.resume(token.accessToken)
                }


            }
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else if (token != null) {

                        Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    }
                }
            } else {
                // token 받아옴
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }


}