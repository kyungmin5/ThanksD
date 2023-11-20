package com.example.thanksd.httpconnection

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.thanksd.login.dataclass.ClientInformation
import com.example.thanksd.login.dataclass.ClientInformation.email
import com.example.thanksd.login.dataclass.ClientInformation.token
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class HttpFunc(private val url: String) {
    // data를 주고받기 위한 scope 생성
    private val scope = CoroutineScope(Dispatchers.Main)

    fun POST(params: JSONObject,viewModel: JsonViewModel){
        var response:JSONObject? = null
        scope.launch {
            val postResult = withContext(Dispatchers.IO) {
                // 네트워크 통신을 위해 IO 스레드에서 실행
                try {
                    response = HttpURLConn().POST(url,params)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            // UI 업데이트 로직
            postResult?.let {
                // viewModel로 전달된 response 값 관찰해서 return 받은 json 오브젝트 활용하면 됩니다.
                viewModel.response.value = response
            }
        }
    }
    fun GET() {
        scope.launch {
            // 백그라운드 작업을 시작하기 전에 실행되는 부분 (예: Dialog 표시)
            val getResult = withContext(Dispatchers.IO) {
                // 네트워크 통신을 위해 IO 스레드에서 실행
                try {
                    HttpURLConn().GET(url)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            // onPostExecute에 해당하는 작업 수행 (예: 결과를 UI에 업데이트)
            getResult?.let {
                // UI 업데이트 로직

            }
        }
    }

    fun cancel() {
        scope.cancel()
        // 작업이 취소되었을 때 수행되는 부분 (예: 취소 관련 UI 처리)
    }
}