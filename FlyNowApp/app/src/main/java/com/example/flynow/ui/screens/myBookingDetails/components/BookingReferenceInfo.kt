package com.example.flynow.ui.screens.myBookingDetails.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import java.util.Locale

//Component that shows an image at the top of the corresponding screen and the booking reference
@Composable
fun BookingReferenceInfo(
    sharedViewModel: SharedViewModel,
    imageId: Int
){
    Image(
        painter = painterResource(id = imageId),
        contentDescription = "baggage",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f)
            .padding(start = 10.dp, end = 10.dp)
    )
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(
            Icons.Filled.AirplaneTicket,
            contentDescription = "bookingId",
            tint = Color(0xFF023E8A),
            modifier = Modifier.padding(start = 10.dp, top = 13.dp)
        )
        Text(
            text = "Booking Reference: ",
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
        Text(
            text = sharedViewModel.textBookingId.uppercase(Locale.getDefault()),
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 13.5.dp),
            color = Color(0xFF0077FF),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            )
        )
    }

    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp),
        thickness = 1.dp,
        color = Color(0xFF023E8A)
    )
}