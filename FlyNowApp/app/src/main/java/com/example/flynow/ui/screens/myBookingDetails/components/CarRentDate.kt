package com.example.flynow.ui.screens.myBookingDetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel

//Component that shows the pick-up and return date of the car rent
@Composable
fun CarRentDate(
    sharedViewModel: SharedViewModel,
    index: Int,
    action: String
){
    Row {
        Text(
            text = action,
            fontSize = 18.sp,
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.gilroy
                    )
                )
            ),
            modifier = Modifier.padding(
                top = 30.dp,
                end = 9.dp
            ),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF023E8A)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text =
                    if(action == "Pick Up")
                        sharedViewModel.carsMyBooking[index]!!.pickUpDateTime.substring(0,10)
                    else
                        sharedViewModel.carsMyBooking[index]!!.returnDateTime.substring(0,10),
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.lato
                        )
                    )
                ),
                modifier = Modifier.padding(top = 20.dp),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF023E8A)
            )
            Text(
                text =
                    if(action == "Pick Up")
                        sharedViewModel.carsMyBooking[index]!!.pickUpDateTime.substring(11,16)
                    else
                        sharedViewModel.carsMyBooking[index]!!.returnDateTime.substring(11,16),
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.lato
                        )
                    )
                ),
                modifier = Modifier.padding(
                    top = 3.dp,
                    bottom = 5.dp
                ),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF023E8A)
            )
        }
    }
}