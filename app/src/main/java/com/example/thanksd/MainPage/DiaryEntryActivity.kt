package com.example.thanksd.MainPage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SwipeProgress
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.thanksd.R
import com.example.thanksd.asmr.MoreQuotesActivity
import com.example.thanksd.login.dataclass.ClientInformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.kakao.sdk.story.model.StoryImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiaryEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Intent에서 date 값을 가져옴
            val receivedIntent = intent
            val date = receivedIntent.getStringExtra("DATE_KEY")
            val listOfImage = listOf(R.drawable.body, R.drawable.girl, R.drawable.girl_2)
            Stories(listOfImage, date)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Stories(listOfImage: List<Int>, date: String?){
    val pagerState = rememberPagerState(pageCount = listOfImage.size)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
//    Log.d("dateCheck", date.toString())

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
fun StoryImage(pagerState: PagerState, listOfImage: List<Int>) {
    HorizontalPager(state = pagerState, dragEnabled = false) {
        Image(painter = painterResource(id = listOfImage[it]), contentDescription = null,
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
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
