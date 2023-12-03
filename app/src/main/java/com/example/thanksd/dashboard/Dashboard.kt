package com.example.thanksd.dashboard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.thanksd.MainPage.component.DaysOfWeekTitle
import com.example.thanksd.Retrofit.RetrofitManager
import com.example.thanksd.dashboard.component.CalendarNav
import com.example.thanksd.dashboard.data.DiaryInfo
import com.example.thanksd.dashboard.data.toList
import com.example.thanksd.dashboard.ui.theme.Brown
import com.example.thanksd.dashboard.ui.theme.PrimaryYellow
import com.example.thanksd.dashboard.utils.getMonthNameFromDate
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DashBoard() {
    // 레트로핏 인스턴스
    var retrofitManager = RetrofitManager.instance

    // feched data
    var dateData by remember {mutableStateOf<List<DiaryInfo>>(emptyList())}
    var weekData by remember { mutableStateOf(listOf(0, 0, 0, 0, 0, 0, 0))}
    var weekMaxCount  by remember { mutableIntStateOf(1) }
    var monthData by remember { mutableStateOf<List<String>>(emptyList()) }


    // 캘린더 관련 상태 변수
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val coroutineScope = rememberCoroutineScope()
    val daysOfWeek = remember { daysOfWeek() }
    val startMonth = remember { selectedDate.yearMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { selectedDate.yearMonth.plusMonths(100) } // Adjust as needed
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth=selectedDate.yearMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    LaunchedEffect(selectedDate){
        val newDate = selectedDate
        var dateResponse = retrofitManager.getDiariesByDate(date=newDate.format(formatter))
        Log.d("dateResponse", "${selectedDate} ${dateResponse}")
        if(dateResponse?.message?.equals("OK")!!){
            dateResponse?.data?.diaryList?.let{
                dateData = it

            }
        }

        var weekResponse = retrofitManager.getDiariesByWeek(date=newDate.format(formatter))
        if(weekResponse?.message?.equals("OK")!!){
            weekResponse?.data?.weekCounts?.let{
                weekData = it.toList()
                if(it.toList().max()!=0){
                    weekMaxCount = it.toList().max()
                }
            }
        }
        var monthResponse = retrofitManager.getDiariesByMonth(year=newDate.year.toString(), month=newDate.month.value.toString())
        if(monthResponse?.message?.equals("OK")!!){
            monthResponse?.data?.dateList?.let{
                monthData = it
            }
        }

    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 20.dp),
    ){
        LazyColumn() {

            // 네비게이션
            item {
                CalendarNav(
                    formatter = {
                        val dateString = it.format(formatter)
                         "${getMonthNameFromDate(dateString)} ${dateString.substring(8,10)}"
                    },
                    current = selectedDate,
                    goToPrevious = {
                        val updatedDate = selectedDate.minusDays(1)
                        if(selectedDate.yearMonth.toString() != updatedDate.yearMonth.toString()){
                            coroutineScope.launch {
                                state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                            }
                        }
                        selectedDate = updatedDate

                    },
                    goToNext = {
                        val updatedDate = selectedDate.plusDays(1)
                        if(!updatedDate.isAfter(LocalDate.now())){
                            if(selectedDate.yearMonth.toString() != updatedDate.yearMonth.toString()){
                                coroutineScope.launch {
                                    state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                                }
                            }
                            selectedDate = updatedDate
                        }
                    },
                )
            }

            // 일별 주간 일기 정보
            item{
                Column(modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillParentMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 15.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Day",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold // 굵은 글꼴 지정
                    )
                    LazyRow {
                        if(dateData.size != 0){
                            items(dateData){ item  ->
                                AsyncImage(modifier=Modifier.fillMaxWidth().size(65.dp).padding(5.dp)
                                    .clip(CircleShape),
                                    model="https://thanksd-image-bucket.s3.ap-northeast-2.amazonaws.com/${item.imageUrl}",
                                    contentDescription = "Diary Thumbnail",
                                    contentScale = ContentScale.Crop)
                            }
                        }else{
                            item{
                                Text(
                                    modifier = Modifier
                                        .heightIn(min=65.dp)
                                        .padding(bottom = 15.dp)
                                        .wrapContentHeight(align = Alignment.CenterVertically)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    text = "No Saved Diaries 🥲",
                                    fontSize = 17.sp,
                                    color=Color.Gray,
                                    fontWeight = FontWeight.Bold // 굵은 글꼴 지정
                                )
                            }
                        }

                    }
                }
            }


            // 주간 일기 정보
            item{
                Column(modifier = Modifier
                    .padding(bottom = 15.dp)
                    .fillParentMaxHeight(0.5f)
                    .fillParentMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 15.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Week",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold // 굵은 글꼴 지정
                    )
                    LazyRow() {
                        itemsIndexed(weekData){
                                index, item-> VerticalGraphBar(max=weekMaxCount, count=item, index=index, isSelected=(selectedDate.dayOfWeek.value -1 == index))
                        }
                    }
                }
            }

            // 월별 일기 정보
            item {
                Column(modifier = Modifier
                    .padding(bottom = 30.dp)
                    .fillParentMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally){
                    Text(
                        text = "Month",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold // 굵은 글꼴 지정
                    )
                    Box(modifier = Modifier
                        .padding(20.dp)
                        .shadow(5.dp, shape = RoundedCornerShape(10.dp))
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(23.dp),
                    ){
                        HorizontalCalendar(
                            state = state,
                            dayContent = { Day(day=it, isSelected=(selectedDate == it.date), isDiaryExist =(it.date.format(formatter) in monthData)) },
                            monthHeader = { DaysOfWeekTitle(daysOfWeek = daysOfWeek) },
                            userScrollEnabled=false,
                        )

                    }

                }

            }
        }
    }

}



@Composable
fun Day(day: CalendarDay, isDiaryExist: Boolean, isSelected: Boolean) {
    var isSunday = day.date.dayOfWeek == DayOfWeek.SUNDAY

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(5.dp)
            .background(
                color = if (isSelected) PrimaryYellow else Color.Transparent,
                shape = CircleShape
            )
            .then(
                if (isDiaryExist) Modifier.border(
                    2.dp,
                    PrimaryYellow,
                    shape = MaterialTheme.shapes.extraLarge
                ) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.date.dayOfMonth.toString(), color=if(isSelected) Color.White else if(isDiaryExist) PrimaryYellow else if(isSunday) Color.Red else Color.Black)
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {

            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color=(if(dayOfWeek == DayOfWeek.SUNDAY) Color.Red else Color.Black)
            )
        }
    }
}


@Composable
fun VerticalGraphBar(max: Int, count: Int, index: Int, isSelected: Boolean){
    val ratio = count.toFloat() / max.toFloat()
    var dayNames= listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    Column(modifier = Modifier
        .padding(horizontal = 10.dp)
        .fillMaxSize()
    ){
        Box(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxHeight(0.8f)
            .padding(bottom = 5.dp)
            .width(8.dp)
            .clip(shape = RoundedCornerShape(25.dp))
            .background(color = Brown.copy(alpha = 0.25f))
            ){
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(ratio)
                .background(color = Brown)
                .align(
                    Alignment.BottomCenter
                )){
            }
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            text= "$count",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color=(if(isSelected) PrimaryYellow else Color.Black)
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            text= dayNames[index],
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color=(if(isSelected) PrimaryYellow else Color.Black)
        )

    }

}


//fun getCurrentDate(): String {
//    val currentDate = LocalDate.now()
//    return currentDate.format(formatter)
//}