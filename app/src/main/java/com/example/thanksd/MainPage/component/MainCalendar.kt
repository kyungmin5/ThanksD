package com.example.thanksd.MainPage.component


import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thanksd.Retrofit.RetrofitManager
import com.example.thanksd.dashboard.DaysOfWeekTitle
import com.example.thanksd.dashboard.ui.theme.DarkBrown
import com.example.thanksd.dashboard.ui.theme.DarkGreen
import com.example.thanksd.dashboard.ui.theme.PrimaryYellow
import com.example.thanksd.dashboard.utils.displayText
import com.example.thanksd.editor.EditorActivity
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun MainCalendar() {
    // 레트로핏 인스턴스
    var retrofitManager = RetrofitManager.instance

    // feched data
    var monthData by remember { mutableStateOf<List<String>>(emptyList()) }


    // 캘린더 관련 상태 변수
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedMonth by remember { mutableStateOf(LocalDate.now().yearMonth) }
    val coroutineScope = rememberCoroutineScope()
    val daysOfWeek = remember { daysOfWeek() }
    val startMonth = remember { selectedDate.yearMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { LocalDate.now().yearMonth} // Adjust as needed
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth=selectedMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    LaunchedEffect(selectedMonth){
        var newMonth = selectedMonth
        var monthResponse = retrofitManager.getDiariesByMonth(year=newMonth.year.toString(), month=newMonth.month.value.toString())
        if(monthResponse?.message?.equals("OK")!!){
            monthResponse?.data?.dateList?.let{
                Log.d("출력중", it.toString())
                monthData = it
            }
        }
    }

    Column(modifier = Modifier
        .padding(bottom = 30.dp)
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally){
        Box(modifier = Modifier
            .padding(20.dp)
            .shadow(5.dp, shape = RoundedCornerShape(10.dp))
            .clip(shape = RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(23.dp),
        ){
            Column {
                MainCalendarNav(current=selectedMonth,
                    goToPrevious={
                        coroutineScope.launch {
                            state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                            selectedMonth = selectedMonth.previousMonth
                        }
                    },
                    goToNext={
                        coroutineScope.launch {
                            if(!selectedMonth.nextMonth.isAfter(LocalDate.now().yearMonth)){
                                state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                                selectedMonth = selectedMonth.nextMonth
                            }
                        }
                    })
                DaysOfWeekTitle(daysOfWeek = daysOfWeek)

                val context = LocalContext.current
                HorizontalCalendar(
                    state = state,
                    dayContent = { Day(day=it, isSelected=(selectedDate == it.date),
                        isDiaryExist =(it.date.format(formatter) in monthData),
                        monthData=monthData,
                        onClick={ clickedDate ->
                            selectedDate=clickedDate.date
                            if(clickedDate.date.isEqual(LocalDate.now())){
                                val intent = Intent(context, EditorActivity::class.java)
                                context.startActivity(intent)
                            }else{
                                Log.d("date 출력 확인", it.date.toString())
                                val intent = Intent(context, DiaryEntryActivity::class.java)
                                intent.putExtra("DATE_KEY", it.date.toString()) // "DATE_KEY"라는 키로 date 값을 DiaryEntryActivity로 전달
                                context.startActivity(intent)
                                // 일기가 있으면 뷰어 보기 -> 클릭 이벤트
                            }
                        })},
                )
            }


        }
    }

}


@Composable
fun MainCalendarNav(
    current: YearMonth,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            modifier = Modifier.padding(horizontal = 15.dp)
                .size(45.dp),
            onClick=goToPrevious
        ){
            Icon(modifier = Modifier.size(34.dp), imageVector = Icons.Rounded.ChevronLeft, contentDescription = "Previous", tint= DarkBrown)
        }
        Text(
            text = current.displayText(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )

        IconButton(
            modifier = Modifier.padding(horizontal = 15.dp)
                .size(45.dp),
            onClick=goToNext
        ){
            Icon(modifier = Modifier.size(34.dp), imageVector = Icons.Rounded.ChevronRight, contentDescription = "Next", tint= DarkBrown)
        }
    }
}




@Composable
fun Day(day: CalendarDay, isDiaryExist: Boolean, isSelected: Boolean,  onClick: (CalendarDay) -> Unit, monthData: List<String>) {
    var isSunday = day.date.dayOfWeek == DayOfWeek.SUNDAY
    var today = LocalDate.now()
    val context = LocalContext.current


    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(5.dp)
            .background(
                color = if (isSelected) PrimaryYellow else Color.Transparent,
                shape = CircleShape
            )
            .clickable(
                enabled = (day.position == DayPosition.MonthDate && !today.isBefore(day.date)),
                onClick = { onClick(day) }
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
        Text(modifier = Modifier.alpha(if(day.position == DayPosition.MonthDate && !today.isBefore(day.date)) 1f else 0.5f),
            text = day.date.dayOfMonth.toString(),
            color=if(isSelected) Color.White else if (today.isEqual(day.date)) DarkGreen else if(isDiaryExist) PrimaryYellow
            else if(isSunday) Color.Red else Color.Black,
            fontWeight =(if(today.isEqual(day.date)) FontWeight.Bold else FontWeight.Normal))
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

