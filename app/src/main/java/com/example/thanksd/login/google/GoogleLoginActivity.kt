package com.example.thanksd.login.google

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.example.thanksd.BuildConfig
import com.example.thanksd.MainPage.MainActivity
import com.example.thanksd.httpconnection.HttpFunc
import com.example.thanksd.httpconnection.JsonViewModel
import com.example.thanksd.login.LoginActivity
import com.example.thanksd.login.dataclass.ClientInformation
import com.example.thanksd.login.google.ui.theme.ThanksDTheme


import org.json.JSONObject

class GoogleLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThanksDTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GoogleLoginScreen()
                }
            }
        }
    }
}

@Composable
fun GoogleLoginScreen() {
    // 웹뷰를 표시
    val cur = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val redirecturl = BuildConfig.Redirecturl
    val clientId = BuildConfig.Client_id
    var check = remember {
        mutableStateOf(-1)
    }
    val url = "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id="+
            clientId+"&redirect_uri="+redirecturl+"&scope=email profile"

//    val cookieManager = CookieManager.getInstance()
//    cookieManager.removeAllCookies(null)
//    cookieManager.flush()
    LaunchedEffect(check.value){
        if(check.value == 1){
            if(ClientInformation.isRegistered)
                cur.startActivity(Intent(cur, MainActivity::class.java))
            else{

            }
        }else if(check.value == 0){
            cur.startActivity(Intent(cur, LoginActivity::class.java))
        }
    }
    val response = JsonViewModel()
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                 // 기본 WebViewClient 사용
                webViewClient = object: WebViewClient(){
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        if (url.startsWith(redirecturl)) {
                            val uri = Uri.parse(url)
                            val authCode =  uri.getQueryParameter("code")
                            if(authCode != null) {
                                Log.d("google_login", authCode)
                                val serverurl = "http://43.202.215.181:8080/login/google"
                                val JsonObj = JSONObject()
                                val httpManager = HttpFunc(serverurl)
                                JsonObj.put("code",authCode)
                                httpManager.POST(JsonObj,response)
//                                accessToken.value = authCode
                                // 사용자 정보 저장 코드 필요
                                return true
                            }
                            else{
                                Log.d("google_login","failed to get authcode")
//                                check.value = 0
                            }
                            // 인증 코드를 처리하는 로직
                        }
                        return false
                    }

                }

                val userAgentString = this.settings.userAgentString
                val newUserAgentString = userAgentString
                    .replace("; wv", "")
                    .replace("Android ${Build.VERSION.RELEASE};", "")
//                this.settings.userAgentString ="Mozilla/5.0 AppleWebKit/535.19 Chrome/56.0.0 Mobile Safari/535.19"
                this.settings.userAgentString = newUserAgentString
                this.settings.javaScriptEnabled = true
                loadUrl(url!!)
//                loadUrl("http://www.naver.com")
            // Google 로그인 URL
//                loadUrl("https://accounts.google.com/o/oauth2/v2/auth")
//                Log.d("google_login", "check")
            }


        }
    )
    response.response.observe(lifecycleOwner, Observer {response->
        val data = JSONObject(response?.get("data").toString())
        val token = data.get("token").toString()
        val email = data.get("email").toString()
        val isRegistered = data.get("isRegistered").toString().toBoolean()
        val platformId = data.get("platformId").toString()
        ClientInformation.updateValue(token,email,isRegistered,platformId)
        check.value = 1
        Log.d("check12", ClientInformation.platformID)
    })
}


//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview2() {
//    ThanksDTheme {
//        Greeting2("Android")
//    }
//}