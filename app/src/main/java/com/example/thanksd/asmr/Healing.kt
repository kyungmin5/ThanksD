package com.example.thanksd.asmr

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import com.example.thanksd.asmr.dataclass.mediaItem
import com.example.thanksd.asmr.dataclass.mediaViewModel
import com.example.thanksd.asmr.dataclass.youtubeData
import com.example.thanksd.asmr.dataclass.youtubeData.videoIDs
import com.example.thanksd.asmr.music.YoutubeURL
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.Duration.Companion.seconds

class Healing {
    lateinit var list: MutableStateFlow<ArrayList<mediaItem>>
    var streamurl = MutableStateFlow<String>("")
    lateinit var exoPlayer:ExoPlayer
    lateinit var mediaViewModels: ArrayList<mediaViewModel>

    companion object {
        val contentNum = 10
    }

    private fun setPlaylist(context: Context, lifeCycleOwner:LifecycleOwner){
        //viewModelList = ArrayList<mediaViewModel>()
        list = MutableStateFlow<ArrayList<mediaItem>>(ArrayList<mediaItem>())
        mediaViewModels = ArrayList<mediaViewModel>()
        val urlManager = YoutubeURL(context)
        for(items in videoIDs){
            mediaViewModels.add(mediaViewModel())
        }
        if(youtubeData.isReady){
            for(item in youtubeData.youtueItems) {
                list.value = ArrayList(list.value).apply { add(item) }
            }
            return
        }
        var i =0
        youtubeData.youtueItems.clear()
        for(videoID in videoIDs){
            urlManager.generateURL(videoID, mediaViewModels[i])
            mediaViewModels[i].url.observe(lifeCycleOwner, Observer { item ->
                list.value = ArrayList(list.value).apply { add(item) }
                youtubeData.youtueItems.add(item)
                if(youtubeData.youtueItems.size == contentNum) {
                    youtubeData.isReady = true
                }
//                Log.d("YoutubeURL",item.title)
            })
            i += 1
        }


    }

    @Composable
    fun healing(){
        val scrollstate = rememberScrollState()
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
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(15.dp, 30.dp, 15.dp, 55.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(30.dp), // 간격 30dp
                modifier = Modifier.verticalScroll(scrollstate)
            ){
                //TODO 화면 구성 함수 호출
                Text(
                    text = "Today's Quotes",
                    textAlign = TextAlign.Start,
                    fontSize = 25.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
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
                                val intent = Intent(context, MoreQuotesActivity::class.java)
                                context.startActivity(intent)
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
                Column(
                    modifier = Modifier.padding(bottom = 55.dp)
                ) {
                    Text(
                        text = "Sounds",
                        textAlign = TextAlign.Start,
                        fontSize = 25.sp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ){
                        asmr()
                    }
                }


            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .requiredWidth(LocalConfiguration.current.screenWidthDp.dp + 30.dp)
            ){
                player()
            }
        }
    }
    @Composable
    fun todayQuotes() {
        val quote = remember { getRandomQuote() } // QuotesData에서 무작위 명언 가져오기

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(Color(0xFFAC9C7C), shape = RoundedCornerShape(8.dp))
                .padding(10.dp)
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
        val quotesList = remember {
            mutableListOf<Quote>()
        }

        // 여기서 10개의 명언을 무작위로 가져와서 목록에 추가
        repeat(10) {
            val randomQuote = getRandomQuote()
            quotesList.add(randomQuote)
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            items(quotesList) { quote ->
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .background(Color(0xFFA5A19E), shape = RoundedCornerShape(8.dp))
                        .width(200.dp)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "${quote.content} - ${quote.author}",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    @Composable
    fun asmr(){
        playlistView()
    }

    @Composable
    fun playlistView(){
        val arr by list.collectAsState()
//        var streamurl = remember {
//            mutableStateOf("")
//        }
        val chunckedarr = arr.chunked(2)
//        val scrollstate = rememberScrollState()
        Column(
//            Modifier.verticalScroll(scrollstate)
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
                                .height(170.dp)
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
                                        .height(130.dp)
                                )
                                Text(text = item.title,
                                    modifier = Modifier
                                        .width(175.dp)
                                        .height(36.dp)
                                )
                            }
                        }
                    }
                }

            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError", "StateFlowValueCalledInComposition")
    @Composable
    fun player(){
//        val item by url
        val item = streamurl.collectAsState()
        val context = LocalContext.current
        exoPlayer = remember(context) {
            ExoPlayer.Builder(context)
                .build()
                .apply {
                    //Log.d("YoutubeURL",item)
                    setMediaItem(MediaItem.fromUri(item.value))
                    prepare()
                    play()
                }
        }
        LaunchedEffect(item.value){
            exoPlayer.apply {
                //Log.d("YoutubeURL",item)
                setMediaItem(MediaItem.fromUri(item.value))
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
                    .height(60.dp)
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
                Log.d("YoutubeURL","onDispose")
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
        val duration = exoPlayer.duration.coerceAtLeast(0)
        var currentPosition = exoPlayer.currentPosition.coerceIn(0, duration)

        var sliderPosition by remember { mutableFloatStateOf(currentPosition.toFloat()) }

        // ExoPlayer의 현재 위치를 업데이트
        if(isPlaying) {
            LaunchedEffect(Unit) {
                Log.d("check12", "slider")
                while(true) {
                    currentPosition = exoPlayer.currentPosition
                    sliderPosition = currentPosition.toFloat()
                    delay(1.seconds / 30)
                }

            }
        }

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
                visible = true,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Gray
                                .copy(alpha = 0.6f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(0.dp, 8.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    ){
                        Icon(
                            imageVector = if(isPlaying)Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Slider(
                            value = sliderPosition,
                            onValueChange = { newPosition ->
                                sliderPosition = newPosition
                                exoPlayer.seekTo(newPosition.toLong())
                            },
                            onValueChangeFinished = {
                                exoPlayer.seekTo(sliderPosition.toLong())
                            },
                            valueRange = 0f..duration.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color.Gray
                            ),
                            modifier = Modifier.width(400.dp)

                        )

                    }
                }
            }

        }
    }
}