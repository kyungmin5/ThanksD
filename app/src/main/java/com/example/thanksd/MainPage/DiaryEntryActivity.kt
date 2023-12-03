package com.example.thanksd.MainPage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.thanksd.MainPage.dataclass.DiaryItem
import com.example.thanksd.MainPage.dataclass.DiaryResponse
import com.example.thanksd.R
import com.example.thanksd.Retrofit.RetrofitClient
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.ImageOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

lateinit var diarydataListFlow : MutableStateFlow<ArrayList<DiaryItem>>
class DiaryEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Intent에서 date 값을 가져옴
            val receivedIntent = intent
            val date = receivedIntent.getStringExtra("DATE_KEY")
            val context = LocalContext.current
            getDiaryByDate(date!!, context)
            SetContent(date = date!!)

            Log.d("date 확인", date.toString())
        }
    }
}

@Composable
fun SetContent(date:String){

    val context = LocalContext.current
    val diarydataList by diarydataListFlow.collectAsState()
    var imageList = ArrayList<String>()

    if (diarydataList.isNotEmpty()) {
        Log.d("너지?", diarydataList.toString())

        for (i in diarydataList.indices){
            imageList.add("https://thanksd-image-bucket.s3.ap-northeast-2.amazonaws.com/${diarydataList[i].image}")
            Log.d("얜가?", diarydataList[i].toString())
        }
        Stories(imageList, date)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Stories(listOfImage: List<String>, date: String?){
    val pagerState = rememberPagerState(pageCount = listOfImage.size)
    Log.d("사이즈 확인", listOfImage.size.toString())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Log.d("dateCheck", date.toString())

    var currentPage by remember{
        mutableStateOf(0)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                // 좌측 클릭
                if (offset.x < size.width / 2) {
                    if (currentPage > 0) {
                        currentPage--
                        coroutineScope.launch {
                            pagerState.scrollToPage(currentPage)
                        }
                    }
                }
                // 우측 클릭
                else {
                    if (currentPage < listOfImage.size - 1) {
                        currentPage++
                        coroutineScope.launch {
                            pagerState.scrollToPage(currentPage)
                        }
                    }
                }
            }
        }
    ){

        StoryImage(pagerState = pagerState, listOfImage = listOfImage)
        // 날짜 표시 (왼쪽 상단)
        date?.let {
            Text(
                text = it,
                color = Color.Gray,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp, top = 20.dp)
            )
        }

        // 'X' 버튼 (오른쪽 상단)
        IconButton(
            onClick = {
                // 클릭 이벤트 처리
//                diarydataList.clear()
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Text(
                text = "X",
                color = Color.Gray,
                fontSize = 20.sp
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            Spacer(modifier = Modifier.padding(4.dp))

            // 이미지 숫자에 맞춰서 스토리 개수 형성
            for (index in listOfImage.indices){
                LinearIndicator(modifier = Modifier.weight(1f),
                    index == currentPage) {
                    coroutineScope.launch {

                        if(currentPage < listOfImage.size - 1){
                            currentPage++
                        }
                        pagerState.animateScrollToPage(currentPage)
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StoryImage(pagerState: PagerState, listOfImage: List<String>) {
    HorizontalPager(state = pagerState, dragEnabled = false) {
        Image(
            painter = // Optional: Add crossfade animation
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = listOfImage[it]).apply(block = fun ImageRequest.Builder.() {
                    crossfade(true) // Optional: Add crossfade animation
                }).build()
            ),
            contentDescription = null, // Add proper content description
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentScale = ContentScale.Crop
        )
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinearIndicator(modifier: Modifier, startProgress: Boolean = false, onAnimationEnd: () -> Unit) {

    var progress by remember{
        mutableStateOf(0.00f)
    }

    val animatedProgess by animateFloatAsState(targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec)

    if (startProgress){
        LaunchedEffect(key1 = Unit){
            while (progress < 1f){
                progress += 0.01f
                delay(50)
            }

            onAnimationEnd()
        }
    }

    LinearProgressIndicator(
        trackColor = Color.LightGray,
        color = Color.White,
        modifier = modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(12.dp)),
        progress = animatedProgess
    )
}

fun getDiaryByDate(date : String, context: Context){
    diarydataListFlow = MutableStateFlow<ArrayList<DiaryItem>>(ArrayList<DiaryItem>())
    RetrofitClient.create(context).getDiariesByDate(date).enqueue(object : Callback<DiaryResponse> {
        override fun onResponse(
            call: Call<DiaryResponse>,
            response: Response<DiaryResponse>
        ) {
            if(response.isSuccessful){
//                diarydataListFlow.value.clear()

                val responseBody = response.body()
                responseBody?.data?.diaryList?.let { newList ->

                    for(item in newList)
                        Log.d("newList확인", newList.toString())
                    diarydataListFlow.value = ArrayList(newList)
                    Log.d("SUCCESS", "Setting: newList = ${diarydataListFlow.value}")
                }
            }
            else{       // 받아오는 것을 실패했을 때-> 로그인 실패, 서버 오류
                Log.e("404 error", response.code().toString())
                Log.e("404 error", response.errorBody().toString())
            }
        }

        override fun onFailure(call: Call<DiaryResponse>, t: Throwable) {
//            TODO("Not yet implemented")
        }

    })
}