package com.example.flynow.ui.screens.myBookingDetails.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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

//Component that shows pet information for the booking
@Composable
fun PetsInfo(
    petSize: String
){
    Row {
        Icon(
            Icons.Filled.Pets,
            contentDescription = "pets",
            tint = Color(0xFF023E8A),
            modifier = Modifier.padding(start = 10.dp, top = 12.dp)
        )
        Text(
            text = "Pets",
            fontSize = 22.sp,
            modifier = Modifier.padding(start = 5.dp, top = 10.dp),
            color = Color(0xFF023E8A),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            fontWeight = FontWeight.Bold
        )
    }
    Text(
        text =
            when (petSize) {
                "Small" -> {
                    "Pet Size: Small (<8kg)"
                }
                "Medium" -> {
                    "Pet Size: Medium (<25kg)"
                }
                "Large" -> {
                    "Pet Size: Large (>25kg)"
                }
                else -> {
                    "No Pets Selected"
                }
        },
        fontSize = 18.sp,
        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
        color = Color(0xFF0077FF),
        fontFamily = FontFamily(
            fonts = listOf(
                Font(
                    resId = R.font.opensans
                )
            )
        )
    )
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        thickness = 1.dp,
        color = Color(0xFF023E8A)
    )
}