package com.example.thanksd.dashboard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thanksd.dashboard.ui.theme.DarkBrown
import java.time.LocalDate

@Composable
fun CalendarNav(
    current: LocalDate,
    formatter: (date: LocalDate) -> String,
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
            Icon(modifier = Modifier.size(34.dp), imageVector = Icons.Rounded.ChevronLeft, contentDescription = "Previous", tint=DarkBrown)
        }
        Text(
            text = formatter(current),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )

        IconButton(
            modifier = Modifier.padding(horizontal = 15.dp)
                .size(45.dp),
            onClick=goToNext
        ){
            Icon(modifier = Modifier.size(34.dp), imageVector = Icons.Rounded.ChevronRight, contentDescription = "Next", tint=DarkBrown)
        }
    }
}
