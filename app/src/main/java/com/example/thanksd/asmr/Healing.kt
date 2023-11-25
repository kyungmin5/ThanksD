package com.example.thanksd.asmr

import android.content.Intent
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.example.thanksd.userprofile.ChangeNameActivity

class Healing {
    @Composable
    fun healing(){
        val context = LocalContext.current
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
                //TODO 화면 구성 함수 호출
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ){
                    Text(
                        text = "Today's Quotes",
                        textAlign = TextAlign.Start,
                        fontSize = 25.sp)
                    //TODO today's quote 띄울 컴포저블 함수 구현
                    todayQuotes()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ){
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Text(
                            text = "Quotes",
                            textAlign = TextAlign.Start,
                            fontSize = 25.sp)
                        Spacer(modifier = Modifier.weight(1.0F,true))
                        Text(
                            text = "more >",
                            textAlign = TextAlign.Start,
                            fontSize = 15.sp,
                            color = Color.Gray,
                            modifier = Modifier.clickable {
                                //TODO 클릭시 기능 구현 (quote 상세 페이지)
//                                val intent = Intent(context, ChangeNameActivity::class.java)
//                                context.startActivity(intent)
                            }
                        )

                    }
                    //TODO quote 띄울 컴포저블 함수 구현
                    quotes()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ){
                    Text(
                        text = "Sounds",
                        textAlign = TextAlign.Start,
                        fontSize = 25.sp)
                    //TODO Asmr 구현
                    asmr()
                }


            }
        }
    }
    @Composable
    fun todayQuotes(){

    }


    @Composable
    fun quotes(){

    }

    @Composable
    fun asmr(){

    }

    @Composable
    fun player(){
        val context = LocalContext.current
        val url = ""
        val exoPlayer = remember {
            ExoPlayer.Builder(context)
                .build()
        }
        
    }
}