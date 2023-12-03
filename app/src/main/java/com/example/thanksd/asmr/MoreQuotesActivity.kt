package com.example.thanksd.asmr

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thanksd.MainPage.MainActivity
import com.example.thanksd.MainPage.QuotesData
import com.example.thanksd.MainPage.dataclass.Quote

class MoreQuotesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuotesScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen() {
    val quotesList = getQuotesList() // 모든 명언을 가져옴
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Quotes",
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                // 'Exit' 텍스트를 TopAppBar 우측에 추가
                Text(
                    text = "Exit",
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable {
                        //TODO 클릭시 기능 구현 (quote 상세 페이지)
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(quotesList) { quote ->
                QuoteItem(quote = quote)
            }
        }
    }
}

@Composable
fun QuoteItem(quote: Quote) {
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
                color = Color(0xFF567050),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
        }
    }
}

fun getQuotesList(): List<Quote> {
    return QuotesData.quotesList
}
