package com.example.thanksd.asmr

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.style.ClickableSpan
import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.example.thanksd.MainPage.QuotesData
import com.example.thanksd.MainPage.dataclass.Quote
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.thanksd.R
import com.example.thanksd.asmr.dataclass.mediaItem
import com.example.thanksd.asmr.dataclass.mediaViewModel
import com.example.thanksd.asmr.music.YoutubeURL
import com.example.thanksd.userprofile.ChangeNameActivity
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.thanksd.MainPage.QuotesData
import com.example.thanksd.MainPage.dataclass.Quote
import com.example.thanksd.MainPage.getRandomQuote
import com.example.thanksd.userprofile.ChangeNameActivity

class Healing {
    lateinit var list: MutableStateFlow<ArrayList<mediaItem>>
    lateinit var mediaViewModels: ArrayList<mediaViewModel>
    val videoIDs = listOf(
        "n1WLUReOFcQ",
        "jJ7dJvQji_0",
        "lfs2_LvM7WY",
        "d41r5AAKoXA",
        "ss-QUzI90to"
    )

    private fun setPlaylist(context: Context, lifeCycleOwner:LifecycleOwner){
        //viewModelList = ArrayList<mediaViewModel>()
        list = MutableStateFlow<ArrayList<mediaItem>>(ArrayList<mediaItem>())
        mediaViewModels = ArrayList<mediaViewModel>()
        val urlManager = YoutubeURL(context)
        for(items in videoIDs){
            mediaViewModels.add(mediaViewModel())
        }
        var i =0
        for(videoID in videoIDs){
            urlManager.generateURL(videoID, mediaViewModels[i])
            mediaViewModels[i].url.observe(lifeCycleOwner, Observer { item ->
                list.value = ArrayList(list.value).apply { add(item) }
//                Log.d("YoutubeURL",item.title)
            })
            i += 1
        }


    }

    @Composable
    fun healing(){
        val context = LocalContext.current
        val lifeCycleOwner = LocalLifecycleOwner.current
        val isSetted = remember {
            mutableStateOf(false)
        }
        if(!isSetted.value) {
            //Log.d("YoutubeURL","check")
            setPlaylist(context, lifeCycleOwner)
            isSetted.value=true
        }
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
                Column {
                    Text(
                        text = "Sounds",
                        textAlign = TextAlign.Start,
                        fontSize = 25.sp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ){
                        //TODO Asmr 구현
                        asmr()
                    }
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
        playlistView()
    }

    @Composable
    fun playlistView(){
        val arr by list.collectAsState()
//        for(item in arr){
//            Log.d("YoutubeURL",item.title)
//        }
        var streamurl = remember {
            mutableStateOf("")
        }
        val chunckedarr = arr.chunked(2)
        val scrollstate = rememberScrollState()
        Column(
            Modifier.verticalScroll(scrollstate)
        ) {
            chunckedarr.forEach { pair ->
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pair.forEach {item->
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .height(180.dp)
                                .clickable {
                                    streamurl.value = item.url
                                }
                        ) {
                            Column(
                                //modifier = Modifier.padding(5.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(item.img),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(180.dp)
                                        .height(160.dp)
                                )
                                Text(text = item.title)
                            }
                        }
                    }
                }

            }
            player(streamurl)

        }

    }

    @SuppressLint("UnsafeOptInUsageError")
    @Composable
    fun player(url:MutableState<String>){
        val item by url
        val context = LocalContext.current
        var exoPlayer = remember(item) {
            ExoPlayer.Builder(context)
                .build()
                .apply {
                    //Log.d("YoutubeURL",item)
                    setMediaItem(MediaItem.fromUri(item))
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
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ){
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
            onDispose {
                Log.d("YoutubeURL","check")
                exoPlayer?.let {
                    it.release()
                }

            }
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