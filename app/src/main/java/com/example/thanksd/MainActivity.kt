package com.example.thanksd

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Calendar()
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


@Preview
@Composable
fun CalendarPreview() {
    Calendar()
}
