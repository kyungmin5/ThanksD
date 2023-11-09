package com.example.thanksd.MainPage

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.material.color.ColorResourcesOverride


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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Thanks :D") }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AndroidView(
                    factory = { context ->
                        CalendarView(context).apply {
                            setBackgroundColor(0xFFFFFFFF.toInt())
                            setPadding(5, 5, 5, 5)
                        }
                    },
                    update = { calendarView ->
                        calendarView.setOnDateChangeListener { calenderView, year, month, day ->
                            date = "$year - ${month + 1} - $day"
                        }
                    },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .border(3.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
                )
                Text(text = date)
            }
        }
    )
}

@Composable
fun sample(){
    Column (
        modifier = Modifier.fillMaxSize().background(Color.White),
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
        composable("??") {
            // 세번째 ?
            sample()
        }
        composable("profile") {
            // 유저 프로필
            sample()
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
                        name = "???",
                        route = "??",
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

@Preview
@Composable
fun CalendarPreview() {
    Calendar()
}
