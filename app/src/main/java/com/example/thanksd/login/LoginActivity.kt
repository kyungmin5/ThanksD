package com.example.thanksd.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.thanksd.MainPage.MainActivity
import com.example.thanksd.R
import com.example.thanksd.httpconnection.HttpFunc
import com.example.thanksd.httpconnection.JsonViewModel
import com.example.thanksd.login.dataclass.ClientInformation
import com.example.thanksd.login.dataclass.ClientInformation.token
import com.example.thanksd.login.google.GoogleLoginActivity
import com.example.thanksd.login.kakao.KakaoAuthViewModel
import com.example.thanksd.ui.theme.ThanksDTheme
import com.kakao.sdk.common.util.Utility
import org.json.JSONObject

class LoginActivity : ComponentActivity() {

    private val kakaoAuthViewModel: KakaoAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val keyHash = Utility.getKeyHash(this)
//        Log.d("keyHash", "$keyHash")
        setContent {
            ThanksDTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginView(kakaoAuthViewModel){ intent ->
                        startActivity(intent)
                    }
//                    Greeting("Android",)
                }
            }
        }
    }
}

@Composable
fun LoginView(kakaoViewModel: KakaoAuthViewModel, navigator: (Intent) -> Unit){
    val context = LocalContext.current
    // TODO 로그인 상태 유지
    // 로그인한 유저의 경우
    if(getLoginState(context)){
        // 사용자 정보 설정
        val encryptedPrefs = getEncryptedSharedPreferences(context)
        ClientInformation.token = encryptedPrefs.getString("token",ClientInformation.token)!!
        ClientInformation.email = encryptedPrefs.getString("email",ClientInformation.email)
        ClientInformation.platformID = encryptedPrefs.getString("platformID",ClientInformation.platformID)!!
        ClientInformation.isRegistered = encryptedPrefs.getBoolean("isRegistered", ClientInformation.isRegistered)
        navigator(Intent(context, MainActivity::class.java))
    }

    // 상태 저장
    val isLoggedIn = kakaoViewModel.isLoggedIn.collectAsState()
    val serverurl = "http://43.202.215.181:8080/login/kakao"
    val accessToken = isLoggedIn.value // 실패시 "-1" 저장

    // 네이버 로그인 viewmodel 구현
    val JsonObj : JSONObject = JSONObject()
    val httpManager = HttpFunc(serverurl)
    val response = JsonViewModel()
    val cur = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(accessToken) {
        if (accessToken != "-1") {
            JsonObj.put("token", accessToken)
            httpManager.POST(JsonObj,response)
            response.response.observe(lifecycleOwner, Observer {response->
                val data = JSONObject(response?.get("data").toString())
                val token = data.get("token").toString()
                val email = data.get("email").toString()
                val isRegistered = data.get("isRegistered").toString().toBoolean()
                val platformId = data.get("platformId").toString()
                ClientInformation.updateValue(token,email,isRegistered,platformId)
                // 카카오 로그인 정보 저장
                saveLoginState(context,true)
                Log.d("kakao_login", "로그인 성공")
            })
            navigator(Intent(context, MainActivity::class.java)) // 다음 액티비티 이름 넣어야함
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            // ThankD logo
            ThanksDLogo()
            Column() { // 구글 카카오
                Spacer(modifier = Modifier.height(60.dp))
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painterResource(id = R.drawable.login_with_kakao),
                        modifier = Modifier
                            .clickable {
                                kakaoViewModel.kakaoLogin()
                            }
                            .fillMaxSize(),
                        contentDescription = null
                    )
                }
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painterResource(id = R.drawable.login_with_google),
                        modifier = Modifier
                            .clickable {
                                /* google 로그인 구현 */
//                            viewModel.kakaoLogin()
                                navigator(Intent(context, GoogleLoginActivity::class.java))
//                                navigator(Intent(context, MainActivity::class.java))
                            }
                            .fillMaxSize(),
                        contentDescription = null
                    )
                }
            }
//            Text(accessToken)
        }
    }
}

@Composable
fun ThanksDLogo(){
    Image(
        painter = painterResource(id = R.drawable.thanks__d),
        contentDescription = null,
    )
}

/*로그인 상태 유지 로직*/
fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    return EncryptedSharedPreferences.create(
        context,
        "UserLoginInfo",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
fun saveLoginState(context: Context, isLoggedIn: Boolean) {
    // 새로 로그인 했을 때 호출
    val encryptedPrefs = getEncryptedSharedPreferences(context)
    encryptedPrefs.edit().putBoolean("IsLoggedIn", isLoggedIn).apply()
    encryptedPrefs.edit().putString("token",ClientInformation.token).apply()
    encryptedPrefs.edit().putString("email",ClientInformation.email).apply()
    encryptedPrefs.edit().putString("platformID",ClientInformation.platformID).apply()
    encryptedPrefs.edit().putBoolean("isRegistered", ClientInformation.isRegistered).apply()
}

fun getLoginState(context: Context): Boolean {
    val encryptedPrefs = getEncryptedSharedPreferences(context)
    return encryptedPrefs.getBoolean("IsLoggedIn", false)
}


/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ThanksDTheme {
        Greeting("Android")
    }
}
*/