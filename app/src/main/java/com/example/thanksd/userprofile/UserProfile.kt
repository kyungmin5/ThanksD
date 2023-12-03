package com.example.thanksd.userprofile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.thanksd.R
import com.example.thanksd.httpconnection.HttpFunc
import com.example.thanksd.httpconnection.JsonViewModel
import com.example.thanksd.login.LoginActivity
import com.example.thanksd.login.dataclass.ClientInformation
import com.example.thanksd.login.google.GoogleLoginActivity
import org.json.JSONObject

class UserProfile {
    @Composable
    fun UserInfo(){
        BoxWithConstraints(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(15.dp, 30.dp, 15.dp, 10.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(30.dp) // 간격 30dp
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ){
                    userData()
                }
                Account()
                Notification()
                Extra()
            }
        }
    }
    fun getSharedPrefValue(context: Context): String {
        val prefs = context.getSharedPreferences("UserName",Context.MODE_PRIVATE)
        return prefs.getString("name", "건붕이") ?: "건붕이"
    }
    @Composable
    fun userData(){
        val context = LocalContext.current
//        var text by remember { mutableStateOf(
//            context.getSharedPreferences("UserName",Context.MODE_PRIVATE).getString("name","건붕이")
//        ) }
        var text by remember { mutableStateOf(getSharedPrefValue(context)) }

        // `SharedPreferences` 변경 리스너 설정
        DisposableEffect(Unit) {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
                text = getSharedPrefValue(context)
            }
            val prefs = context.getSharedPreferences("UserName", Context.MODE_PRIVATE)
            prefs.registerOnSharedPreferenceChangeListener(listener)
            onDispose {
                prefs.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            /* user image 코드 필요 */
            Image(
                painterResource(id = R.drawable.default_user_img),
                modifier = Modifier
                    .width(38.dp)
                    .height(38.dp),
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ){
                Text(
                    text = text!!, //TODO 유저 닉네임 지정 형식?
                    textAlign = TextAlign.Start,
                    fontSize = 20.sp)
                Text(
                    text = ClientInformation.email!!,
                    textAlign = TextAlign.Start,
                    fontSize = 10.sp,
                    color = Color.Gray
                )

            }

        }
    }

    @Composable
    fun Account(){
        val context = LocalContext.current
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Image(
                painter = painterResource(id = R.drawable.user_account),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Change Name",
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier.clickable {
                    //TODO 클릭시 기능 구현
                    val intent = Intent(context, ChangeNameActivity::class.java)
                    context.startActivity(intent)
                }
            )
            Text(
                text = "Language",
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier.clickable {
                    //TODO 클릭시 기능 구현
                }
            )
        }
    }

    @Composable
    fun Notification(){
        val context = LocalContext.current
        var isMessageEnabled by remember { mutableStateOf(
            context.getSharedPreferences("MySettings",Context.MODE_PRIVATE).getBoolean("message",false)
        ) }
        var isSoundEnabled by remember { mutableStateOf(
            context.getSharedPreferences("MySettings",Context.MODE_PRIVATE).getBoolean("sound",false)
        ) }
        var isVibrateEnabled by remember { mutableStateOf(
            context.getSharedPreferences("MySettings",Context.MODE_PRIVATE).getBoolean("vibrate",false)
        ) }
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.user_notification),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(10.dp))

            //message 설정
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Message Alerts",
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1.0f)
                )
                Spacer(modifier = Modifier.weight(1.0f))
                Switch(
                    checked = isMessageEnabled,
                    modifier = Modifier.scale(0.6f),
                    onCheckedChange = { isChecked ->
                        context.getSharedPreferences("MySettings", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("message",isChecked)
                            .apply()
                        isMessageEnabled = isChecked
                        if (isChecked) {
                            // 알림 설정
                            //TODO 알림 설정 기능
                            // do nothing
//                    enableNotification(context)
                        } else {
                            // 알림 해제
//                    disableNotification(context)
                        }
                    }
                )
            }

            //Sound 설정
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Sound",
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1.0f)
                )
                Spacer(modifier = Modifier.weight(1.0f))
                Switch(
                    checked = isSoundEnabled,
                    modifier = Modifier.scale(0.6f),
                    onCheckedChange = { isChecked ->
                        context.getSharedPreferences("MySettings", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("sound",isChecked)
                            .apply()
                        isSoundEnabled = isChecked
                        if (isChecked) {
                            // 알림 설정
//                    enableNotification(context)
                        } else {
                            // 알림 해제
//                    disableNotification(context)
                        }
                    }
                )
            }

            /* 진동 설정 */
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Vibrate",
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1.0f)
                )
                Spacer(modifier = Modifier.weight(1.0f))
                Switch(
                    checked = isVibrateEnabled,
                    modifier = Modifier.scale(0.6f),
                    onCheckedChange = { isChecked ->
                        context.getSharedPreferences("MySettings", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("vibrate",isChecked)
                            .apply()
                        isVibrateEnabled = isChecked
                        if (isChecked) {
                            // 알림 설정
//                    enableNotification(context)
                        } else {
                            // 알림 해제
//                    disableNotification(context)
                        }
                    }
                )
            }

        }


    }

    @Composable
    fun Extra(){
        val serverurl = "http://43.202.215.181:8080/members"
        val response = JsonViewModel()
        val httpManager = HttpFunc(serverurl)
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.user_etc),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Sign Out",
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier
//                    .weight(1.0f)
                    .clickable {
                        //TODO 회원 탈퇴 기능 구현
                        AlertDialog.Builder(context)
                            .setTitle("회원 탈퇴 확인")
                            .setMessage("정말로 회원 탈퇴를 하시겠습니까?")
                            .setPositiveButton("탈퇴") { dialog, which ->
                                httpManager.DELETE(response)
                            }
                            .setNegativeButton("취소", null)
                            .show()
                    }
            )
        }
        response.response.observe(lifecycleOwner, Observer {response->
            if(response!=null) {
                val code = response.get("code").toString()
                Log.d("delete_code", code)
                if (code == "200") {
                    Toast.makeText(context, "성공적으로 회원탈퇴 하셨습니다.", Toast.LENGTH_SHORT).show()
                    deleteLoginState(context,false)
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "회원탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "회원탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
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
    fun deleteLoginState(context: Context, isLoggedIn: Boolean) {
        // 새로 로그인 했을 때 호출
        val encryptedPrefs = com.example.thanksd.login.getEncryptedSharedPreferences(context)
        encryptedPrefs.edit().putBoolean("IsLoggedIn", isLoggedIn).apply()
        encryptedPrefs.edit().putString("token","-1").apply()
        encryptedPrefs.edit().putString("email",null).apply()
        encryptedPrefs.edit().putString("platformID","no-info").apply()
        encryptedPrefs.edit().putBoolean("isRegistered", false).apply()
        encryptedPrefs.edit().putString("loginDate","2020-12-12").apply()

        val prefs = context.getSharedPreferences("UserName",Context.MODE_PRIVATE)
        prefs.edit().putString("name","건덕이").apply()

        context.getSharedPreferences("MySettings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("sound",false)
            .apply()
        context.getSharedPreferences("MySettings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("message",false)
            .apply()
        context.getSharedPreferences("MySettings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("vibrate",false)
            .apply()
    }


}