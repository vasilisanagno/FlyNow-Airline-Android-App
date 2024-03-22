package com.example.flynow.ui.screens.more.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.More

//component that shows the list options in the "More" Screen
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ListOption(
    title: String,
    navController: NavController,
    route: String
){
    ClickableText(
        text =  AnnotatedString(title),
        onClick = {
            navController.navigate(route) {
                popUpTo(More.route)
                launchSingleTop = true
            }
        },
        modifier = Modifier
            .padding(top = if (title == "Upgrade to Business Class") 28.dp else 0.dp)
            .height(66.24.dp)
            .border(width = 1.dp, color = Color.LightGray)
            .background(color = Color.White)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically),
        style = TextStyle(
            fontSize = 16.sp,
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color(0xFF023E8A),
            textIndent = TextIndent(20.sp,0.sp)
        )
    )
}