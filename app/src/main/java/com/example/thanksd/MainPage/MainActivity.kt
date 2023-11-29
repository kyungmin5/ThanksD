package com.example.thanksd.MainPage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.http.HttpResponseCache.install
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thanksd.MainPage.dataclass.BottomNavItem
import com.example.thanksd.MainPage.dataclass.DiaryItem
import com.example.thanksd.MainPage.dataclass.Quote
import com.example.thanksd.R
import com.example.thanksd.asmr.Healing
import com.example.thanksd.Retrofit.RetrofitClient
import com.example.thanksd.editor.EditorActivity
import com.example.thanksd.httpconnection.DiaryService
import com.example.thanksd.login.dataclass.ClientInformation
import com.example.thanksd.userprofile.ChangeNameActivity
import com.example.thanksd.userprofile.UserProfile
import com.google.android.material.color.ColorResourcesOverride
import kotlinx.coroutines.CoroutineScope
import okhttp3.internal.userAgent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val userProfile = UserProfile() // userprofile composable fun 저장한 클래스
val healing = Healing()
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            Calendar()
            val navController = rememberNavController()
            Screen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Calendar() {
    var date by remember {
        mutableStateOf("")
    }
    var diaries by remember {
        mutableStateOf<List<DiaryItem>?>(null)
    }
    val context = LocalContext.current
    Log.d("check123", ClientInformation.token)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thanks :D",
                        fontWeight = FontWeight.Bold // 굵은 글꼴 지정
                    )
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 위쪽 고정 텍스트 창
                Text(
                    text = "Hi, user12! How's your day going? Here is Today's quotes for you :)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )

                // 캘린더 뷰 위에 무작위 명언과 작가 이름을 포함한 상자 추가
                RandomQuoteBox()

                // 캘린더 뷰 업데이트하는 로직
                val diaryService = DiaryService()


                AndroidView(
                    factory = { context ->
                        CalendarView(context).apply {
                            setBackgroundColor(0xFFFFFFFF.toInt())
                            setPadding(5, 5, 5, 0)
                        }
                    },
                    update = { calendarView ->
                        calendarView.setOnDateChangeListener { _, year, month, day ->
                            val formattedDate = String.format("%d-%02d-%02d", year, month + 1, day)
                            date = formattedDate

                            /*viewModelScope.launch {
                                diaries = fetchDiariesByDate(formattedDate)
                                // 여기서 diaries를 사용하여 UI를 업데이트하거나 필요한 작업을 수행할 수 있습니다.
                            }*/
                        }
                    },
                    modifier = Modifier
                        .aspectRatio(1.1f)
                        .border(3.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(bottom = 5.dp) // 캘린더뷰 아래 여백 추가
                )
                // 이미지 목록을 보여줄 화면으로 네비게이션
                /*diaries?.let { diaryList ->
                    if (diaryList.isNotEmpty()) {
                        // 이미지 목록이 비어 있지 않을 때 화면 이동
                        ShowDiaryImages(diaryList = diaryList) { imageUrl ->
                            // Glide를 사용하여 이미지 띄우기
                            Glide.with(context)
                                .load(imageUrl)
                                //.into(imageView)
                        }
                    }
                }*/

                // 우측 하단에 흰색 동그라미 아이콘 추가
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(context, EditorActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 16.dp, 16.dp) // + 버튼 여백 조절
                        .size(45.dp)
                        .background(Color.Transparent, shape = CircleShape)
                        .border(1.dp, Color.LightGray, shape = CircleShape)
                        .align(Alignment.End),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.Gray, // '+' 모양의 색상 설정
                        modifier = Modifier
                            .size(24.dp) // 이미지 크기 조절
                    )
                }
            }
        }
    )
}

@Composable
fun ShowDiaryImages(diaryList: List<DiaryItem>, onImageClick: (String) -> Unit) {
    // 여기서 diaryList를 기반으로 이미지 목록을 표시하고, 클릭 이벤트 처리
    // 예시로 이미지 클릭시 onImageClick를 호출하여 Glide로 이미지를 띄워줄 수 있습니다.
}

@Composable
fun sample(){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "오류 방지용입니다")
    }

}


/*바텀 네비게이션 구성*/
@Composable
fun Navigation(navController: NavHostController) {

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            // 메인 화면
            Calendar()
        }
        composable("dashboard") {
            // 대시보드
            sample()
        }
        composable("healing") {
            // 세번째 ?
            healing.healing()
        }
        composable("profile") {
            // 유저 프로필
            userProfile.UserInfo()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation (
        modifier = modifier,
        backgroundColor = Color.White,
        elevation = 5.dp
    ) {
        // items 배열에 담긴 모든 항목을 추가합니다.
        items.forEach { item ->
            // 뷰의 활동 상태를 백스택에 담아 저장합니다.
            val selected = item.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                selectedContentColor = Color.Green,
                unselectedContentColor = Color.Gray,
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        // 뱃지카운트가 1이상이면, 아이콘에 뱃지카운트가 표시됩니다.
                        // 아이콘이 선택 되었을 때, 아이콘 밑에 텍스트를 표시합니다.
                        if (selected) {
                            Image(
                                painter = painterResource(id = R.drawable.onselect),
                                contentDescription = null,
                            )
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }else{
                            Image(
                                painter = painterResource(id = R.drawable.offselect),
                                contentDescription = null,
                            )
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Screen(navController: NavHostController){
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = listOf(
                    BottomNavItem(
                        name = "Home",
                        route = "home",
                    ),
                    BottomNavItem(
                        name = "DashBoard",
                        route = "dashboard",
                        badgeCount = 24
                    ),
                    BottomNavItem(
                        name = "Healing",
                        route = "healing",
                    ),
                    BottomNavItem(
                        name = "User-profile",
                        route = "profile",
                    )

                ),
                navController = navController,
                onItemClick = {
                    navController.navigate(it.route)
                })
        }
    ) {
        Navigation(navController = navController)
    }
}

@Composable
fun RandomQuoteBox() {
    val quote = remember { getRandomQuote() } // QuotesData에서 무작위 명언 가져오기

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFB8805d), shape = RoundedCornerShape(8.dp))
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
                color = Color(0xFF006400) // 짙은 초록색
            )
        }
    }
}

// QuotesData에서 무작위 명언과 작가 이름을 가져오는 함수
fun getRandomQuote(): Quote {
    val randomIndex = (0 until QuotesData.quotesList.size).random()
    return QuotesData.quotesList[randomIndex]
}

// Retrofit으로 네트워크 요청
suspend fun fetchDiariesByDate(date: String): List<DiaryItem>? {
    return withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.diaryService.getDiariesByDate(date)
            if (response.code == "200" && response.data != null) {
                response.data.diaryList
            } else {
                null
            }
        } catch (e: Exception) {
            // 네트워크 오류 또는 예외 처리
            e.printStackTrace()
            null
        }
    }
}

@Preview
@Composable
fun CalendarPreview() {
    val navController = rememberNavController()
    Screen(navController)
}
