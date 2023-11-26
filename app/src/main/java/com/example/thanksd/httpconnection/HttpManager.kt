package com.example.thanksd.httpconnection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class HttpManager(private val uri:String) {
    private val scope = CoroutineScope(Dispatchers.Main)

    val serverurl = "http://43.202.215.181:8080/"+uri

    fun POST(params: JSONObject, viewModel: JsonViewModel){
        var response: JSONObject? = null
        scope.launch {
            val postResult = withContext(Dispatchers.IO) {
                // 네트워크 통신을 위해 IO 스레드에서 실행
                try {
                    val url = serverurl
                    response = HttpURLConn().POST(url,params)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            // UI 업데이트 로직
            postResult?.let {
                // viewModel로 전달된 response 값 관찰해서 return 받은 json 오브젝트 활용하면 됩니다.
                viewModel.response.value = response
            }
        }
    }

    //TODO 인자가 필요한 GET의 경우는 함수 오버로딩을 해야할 것 같습니다.
    fun GET(viewModel: JsonViewModel) {
        var response: JSONObject? = null
        scope.launch {
            // 백그라운드 작업을 시작하기 전에 실행되는 부분 (예: Dialog 표시)
            val getResult = withContext(Dispatchers.IO) {
                // 네트워크 통신을 위해 IO 스레드에서 실행
                try {
                    val url = serverurl
                    response = HttpURLConn().GET(url)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            // onPostExecute에 해당하는 작업 수행 (예: 결과를 UI에 업데이트)
            getResult?.let {
                // UI 업데이트 로직
                viewModel.response.value = response
            }
        }
    }
    //TODO
    // 인자가 필요한 DELETE의 경우는 함수 오버로딩을 해야할 것 같습니다. 현재는 json viewmodel 객체만 받습니다
    // 회원탈퇴의 경우는 필요없지만 형식을 지키기 위해 작성했습니다.
    fun DELETE(viewModel: JsonViewModel){
        var response: JSONObject? = null
        scope.launch {
            val deleteResult = withContext(Dispatchers.IO) {
                // 네트워크 통신을 위해 IO 스레드에서 실행
                try {
                    val url = serverurl
                    response = HttpURLConn().DELETE(url)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            // UI 업데이트 로직
            deleteResult?.let {
                // viewModel로 전달된 response 값 관찰해서 return 받은 json 오브젝트 활용하면 됩니다.
                viewModel.response.value = response

            }
        }
    }

    fun cancel() {
        scope.cancel()
        // 작업이 취소되었을 때 수행되는 부분 (예: 취소 관련 UI 처리)
    }
}