package com.example.thanksd.userprofile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thanksd.mainpage.MainActivity
import com.example.thanksd.R
import com.example.thanksd.userprofile.ui.theme.ThanksDTheme

class ChangeNameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThanksDTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    changeName()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun changeName() {
    val context = LocalContext.current
    var text by remember{
        mutableStateOf("")
    }
    BoxWithConstraints(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(15.dp, 15.dp, 15.dp, 15.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp) // 간격 30dp
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(15.dp, 0.dp, 0.dp, 0.dp),
                text = "Change Name",
                textAlign = TextAlign.Start,
                fontSize = 30.sp,
                color = Color.Black
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    OutlinedTextField(
                        modifier = Modifier.width(292.dp),
                        value = text,
                        singleLine = true,
                        label = {Text(
                            text = "New name",
                            textAlign = TextAlign.Start,
                            fontSize = 15.sp,
                            color=Color.Black
                        )},
                        onValueChange = {newValue -> text = newValue},
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black, // 텍스트 색상 변경
                            focusedBorderColor = Color.Gray, // 포커스된 상태의 테두리 색상
                            unfocusedBorderColor = Color.Gray // 포커스되지 않은 상태의 테두리 색상
                        )
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Image(
                        painterResource(id = R.drawable.change_name_btn),
                        modifier = Modifier
                            .width(292.dp)
                            .height(52.dp).clickable {
                                context.getSharedPreferences("UserName", Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("name",text)
                                    .apply()
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                                context.startActivity(intent)
                            },
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ThanksDTheme {
        changeName()
    }
}