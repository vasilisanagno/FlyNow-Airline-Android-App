package com.example.flynow.ui.screens.myBookingDetails.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.screens.myBookingDetails.MyBookingDetailsViewModel

//Button component for deleting a booking
@Composable
fun DeleteBookingButton(
    myBookingDetailsViewModel: MyBookingDetailsViewModel
){
    ElevatedButton(
        onClick = {
            myBookingDetailsViewModel.showDialog = true
        },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(top = 30.dp, bottom = 30.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFa4161a)),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp
        )
    ) {
        Row {
            Text(text = "Delete Your Booking",
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
            Icon(
                Icons.Outlined.DeleteForever,
                contentDescription = "deletion",
                tint = Color.White
            )
        }
    }
}