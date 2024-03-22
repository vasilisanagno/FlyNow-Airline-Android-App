package com.example.flynow.ui.screens.myBookingDetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Euro
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
import com.example.flynow.ui.SharedViewModel

//Component that shows the price of the booking
@Composable
fun PriceInfo(
    sharedViewModel: SharedViewModel,
    priceType: String
){
    if(priceType == "Booking"){
        Row {
            Icon(
                Icons.Outlined.Euro,
                contentDescription = priceType,
                tint = Color(0xFF023E8A),
                modifier = Modifier.padding(start = 10.dp, top = 13.dp)
            )
            Text(
                text = "$priceType Price",
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
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "$priceType Price: ",
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
                text =
                when (priceType) {
                    "Booking" -> "${sharedViewModel.totalPriceMyBooking} €"
                    "Renting Cars" -> "${sharedViewModel.rentingTotalPrice} €"
                    else -> "${sharedViewModel.totalPriceMyBooking + sharedViewModel.rentingTotalPrice} €"
                },
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 10.dp),
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
    }
}
