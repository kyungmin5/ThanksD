package com.example.thanksd.mainpage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thanksd.mainpage.component.MainCalendar
import com.example.thanksd.mainpage.dataclass.BottomNavItem
import com.example.thanksd.mainpage.dataclass.DiaryItem
import com.example.thanksd.mainpage.dataclass.DiaryResponse
import com.example.thanksd.mainpage.dataclass.DiaryResponseByMonth
import com.example.thanksd.mainpage.dataclass.Quote
import com.example.thanksd.R
import com.example.thanksd.retrofit.RetrofitClient
import com.example.thanksd.asmr.Healing
import com.example.thanksd.dashboard.DashBoard
import com.example.thanksd.dashboard.ui.theme.PrimaryYellow
import com.example.thanksd.editor.EditorActivity
import com.example.thanksd.login.dataclass.ClientInformation
import com.example.thanksd.mainpage.QuotesData
import com.example.thanksd.userprofile.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

val userProfile = UserProfile() // userprofile composable fun 저장한 클래스
val healing = Healing()
var datelist = ArrayList<String>()
var diarydataList = ArrayList<DiaryItem>()
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val (year, month) = getCurrentYearAndMonth()
        setContent {
//            Calendar()
            getDateByMonth(year, month, LocalContext.current)
            getDiaryByDate("2023-11-26", LocalContext.current)

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

    val scrollstate = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical=70.dp, horizontal=10.dp)
            ) {
                item{
                    // 위쪽 고정 텍스트 창
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        text = "Hi, user12! How's your day going?\n Here is Today's quotes for you :)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color=Color.Black,
                    )
                }
                //
                item{
                    // 캘린더 뷰 위에 무작위 명언과 작가 이름을 포함한 상자 추가
                    RandomQuoteBox()
                }
                //
                item{
                    // 메인 켈린더
                    MainCalendar()
                }
            }


        },

        )
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
            DashBoard()
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
    val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, EditorActivity::class.java)
                    context.startActivity(intent)
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(45.dp),
                content= {
                    Icon(
                        modifier = Modifier.fillMaxSize()
                            .background(PrimaryYellow)
                            .size(24.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White, // '+' 모양의 색상 설정

                    )
                }
            )
        },
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
                .padding(10.dp)
                .padding(bottom = 4.dp)
        ) {
            Text(
                text = quote.content,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.End,
                text = "- ${quote.author}",
                fontSize = 12.sp,
                color = Color(0xFF006400),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// QuotesData에서 무작위 명언과 작가 이름을 가져오는 함수
fun getRandomQuote(): Quote {
    val randomIndex = (0 until QuotesData.quotesList.size).random()
    return QuotesData.quotesList[randomIndex]
}


fun getDateByMonth(year: Int, month: Int, context: Context){
    Log.d("runing", "running getDateByMonth")
    RetrofitClient.create(context).getDiriesByMonth(year, month).enqueue(object :Callback<DiaryResponseByMonth>{
        override fun onResponse(
            call: Call<DiaryResponseByMonth>,
            response: Response<DiaryResponseByMonth>
        ) {
            if(response.isSuccessful){
                datelist.clear()

                for (i in response.body()!!.data?.diaryList?.indices!!){
                    datelist.add(response.body()!!.data?.diaryList?.get(i).toString())
                    Log.d("SUCCESS", datelist[i].toString())
                }
            }
            else{       // 받아오는 것을 실패했을 때-> 로그인 실패, 서버 오류
                Log.e("404 error", response.code().toString())
                Log.e("404 error", response.errorBody().toString())
                Log.d("message", call.request().toString())
                Log.d("msss", response.message().toString())
            }
        }

        override fun onFailure(call: Call<DiaryResponseByMonth>, t: Throwable) {
            TODO("Not yet implemented")
        }

    })
}

@Composable
fun getDateByMonthComposable(year: Int, month: Int, context: Context){
    RetrofitClient.create(context).getDiriesByMonth(year, month).enqueue(object :Callback<DiaryResponseByMonth>{
        override fun onResponse(
            call: Call<DiaryResponseByMonth>,
            response: Response<DiaryResponseByMonth>
        ) {
            if(response.isSuccessful){
                datelist.clear()

                val responseBody = response.body()
                responseBody?.data?.diaryList?.let { newList ->

                    Log.d("SUCCESS", "Setting: newList = $newList")

                    datelist.addAll(newList)
                }
            }
            else{       // 받아오는 것을 실패했을 때-> 로그인 실패, 서버 오류
                Log.e("404 error", response.code().toString())
                Log.e("404 error", response.errorBody().toString())
            }
        }

        override fun onFailure(call: Call<DiaryResponseByMonth>, t: Throwable) {
            TODO("Not yet implemented")
        }

    })
}


fun getDiaryByDate(date : String, context: Context){
    RetrofitClient.create(context).getDiariesByDate(date).enqueue(object :Callback<DiaryResponse>{
        override fun onResponse(
            call: Call<DiaryResponse>,
            response: Response<DiaryResponse>
        ) {
            if(response.isSuccessful){
                diarydataList.clear()

                val responseBody = response.body()
                responseBody?.data?.diaryList?.let { newList ->

                    Log.d("SUCCESS", "Setting: newList = $newList")

                    diarydataList.addAll(newList)
                }
            }
            else{       // 받아오는 것을 실패했을 때-> 로그인 실패, 서버 오류
                Log.e("404 error", response.code().toString())
                Log.e("404 error", response.errorBody().toString())
            }
        }

        override fun onFailure(call: Call<DiaryResponse>, t: Throwable) {
            TODO("Not yet implemented")
        }

    })
}
@Composable
fun getDiaryByDateComposable(date : String, context: Context){
    RetrofitClient.create(context).getDiariesByDate(date).enqueue(object :Callback<DiaryResponse>{
        override fun onResponse(
            call: Call<DiaryResponse>,
            response: Response<DiaryResponse>
        ) {
            if(response.isSuccessful){
                diarydataList.clear()

                val responseBody = response.body()
                responseBody?.data?.diaryList?.let { newList ->

                    Log.d("TAG", "Setting: newList = $newList")

                    diarydataList.addAll(newList)
                }
            }
            else{       // 받아오는 것을 실패했을 때-> 로그인 실패, 서버 오류
                Log.e("404 error", response.code().toString())
                Log.e("404 error", response.errorBody().toString())
            }
        }

        override fun onFailure(call: Call<DiaryResponse>, t: Throwable) {
            TODO("Not yet implemented")
        }

    })
}

fun getCurrentYearAndMonth(): Pair<Int, Int> {
    val currentDate = LocalDate.now()
    val year = currentDate.year
    val month = currentDate.monthValue
    return Pair(year, month)
}

@Preview
@Composable
fun CalendarPreview() {
    val navController = rememberNavController()
    Screen(navController)
}