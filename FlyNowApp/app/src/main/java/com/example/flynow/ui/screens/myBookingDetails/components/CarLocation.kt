package com.example.flynow.ui.screens.myBookingDetails.components

import androidx.compose.foundation.layout.Row
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
import com.example.flynow.ui.SharedViewModel

//Component that shows the pick-up/return location of the car rental
@Composable
fun CarLocation(
    sharedViewModel: SharedViewModel,
    index: Int
){
    Row {
        Text(
            text = "Location",
            fontSize = 18.sp,
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.gilroy
                    )
                )
            ),
            modifier = Modifier.padding(top = 15.dp),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF023E8A)
        )
        Text(
            text = sharedViewModel.carsMyBooking[index]!!.location,
            fontSize = 18.sp,
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.lato
                    )
                )
            ),
            modifier = Modifier.padding(
                start = 10.dp,
                top = 15.dp
            ),
            color = Color(0xFF023E8A)
        )
    }
}