package com.example.flynow.ui.screens.upgradeClassDetails.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R

//component that shows the services its class offers
@Composable
fun BusinessInfo() {
    Text(
        text = "Some of the Βusiness Class features and services:",
        fontSize = 18.sp,
        modifier = Modifier.padding(start = 10.dp, top = 30.dp),
        fontFamily = FontFamily(
            fonts = listOf(
                Font(
                    resId = R.font.opensans
                )
            )
        ),
        color = Color(0xFF023E8A),
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "1) Airport priorities",
        fontSize = 16.sp,
        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
        fontFamily = FontFamily(
            fonts = listOf(
                Font(
                    resId = R.font.opensans
                )
            )
        ),
        color = Color(0xFF023E8A),
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "2) Business lounges",
        fontSize = 16.sp,
        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
        fontFamily = FontFamily(
            fonts = listOf(
                Font(
                    resId = R.font.opensans
                )
            )
        ),
        color = Color(0xFF023E8A),
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "3) Gastronomics",
        fontSize = 16.sp,
        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
        fontFamily = FontFamily(
            fonts = listOf(
                Font(
                    resId = R.font.opensans
                )
            )
        ),
        color = Color(0xFF023E8A),
        fontWeight = FontWeight.Bold
    )
}