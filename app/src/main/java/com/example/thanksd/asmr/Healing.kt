package com.example.thanksd.asmr

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.thanksd.MainPage.QuotesData
import com.example.thanksd.MainPage.dataclass.Quote
import com.example.thanksd.MainPage.getRandomQuote
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
                Text(
                    text = "Today's Quotes",
                    textAlign = TextAlign.Start,
                    fontSize = 25.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ){
                    //TODO today's quote 띄울 컴포저블 함수 구현
                    todayQuotes()
                }
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ){
                    //TODO quote 띄울 컴포저블 함수 구현
                    quotes()
                }
                Text(
                    text = "Sounds",
                    textAlign = TextAlign.Start,
                    fontSize = 25.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ){
                    //TODO Asmr 구현
                    asmr()
                }


            }
        }
    }
    @Composable
    fun todayQuotes() {
        val quote = remember { getRandomQuote() } // QuotesData에서 무작위 명언 가져오기

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color(0xFFAC9C7C), shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = quote.content,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = "- ${quote.author}",
                    fontSize = 12.sp,
                    color = Color(0xFF567050), // 짙은 초록색
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }
        }
    }

    fun getRandomQuote(): Quote {
        val randomIndex = (0 until QuotesData.quotesList.size).random()
        return QuotesData.quotesList[randomIndex]
    }

    @Composable
    fun quotes(){

    }

    @Composable
    fun asmr(){
        player()
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Composable
    fun player(){
        val context = LocalContext.current
        val url = ""
        val exoPlayer = remember {
            ExoPlayer.Builder(context)
                .build()
                .apply {
                    //TODO uri 연결해야함
                    setMediaItem(MediaItem.fromUri("https://firebasestorage.googleapis.com/v0/b/jetcalories.appspot.com/o/FROM%20EARTH%20TO%20SPACE%20_%20Free%20HD%20VIDEO%20-%20NO%20COPYRIGHT.mp4?alt=media&token=68bce5a0-7ca7-4538-9fea-98d0dc2d3646"))
                    prepare()
                    play()
                }

        }
        var isPlaying by remember {
            mutableStateOf(false)
        }

        exoPlayer.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                    isPlaying = isPlayingValue
                }
            }
        )
        DisposableEffect(
            Box {
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            // Resizes the video in order to be able to fill the whole screen
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            player = exoPlayer
                            // Hides the default Player Controller
                            useController = false
                            // Fills the whole screen
                            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        }
                    }
                )
                VideoLayout(
                    isPlaying = isPlaying,
                    onPlay = {
                        exoPlayer.play()
                    },
                    onPause = {
                        exoPlayer.pause()
                    }
                )
            }
        ) {
            onDispose { exoPlayer.release() }
        }


    }
    @Composable
    fun VideoLayout(
        isPlaying: Boolean,
        onPlay: () -> Unit,
        onPause: () -> Unit
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (isPlaying) {
                        onPause()
                    } else {
                        onPlay()
                    }
                }
        ) {
            AnimatedVisibility(
                visible = !isPlaying,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black
                                .copy(alpha = 0.6f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Pause,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

        }
    }
}