package com.example.thanksd.login

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.thanksd.R
import com.example.thanksd.login.kakao.KakaoAuthViewModel
import com.example.thanksd.ui.theme.ThanksDTheme
import com.kakao.sdk.common.util.Utility

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
                    LoginView(kakaoAuthViewModel)
//                    Greeting("Android",)
                }
            }
        }
    }
}

@Composable
fun LoginView(kakaoViewModel: KakaoAuthViewModel){

    // 상태 저장
    val isLoggedIn = kakaoViewModel.isLoggedIn.collectAsState()

    val accessToken = isLoggedIn.value // 실패시 "-1" 저장

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
                            }
                            .fillMaxSize(),
                        contentDescription = null
                    )
                }
            }
            Text(accessToken)
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


/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ThanksDTheme {
        Greeting("Android")
    }
}
*/