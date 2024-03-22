package com.example.flynow.ui.screens.baggageAndPets.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.WarningAmber
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

//component that shows the first baggage info like image and title of screen
@Composable
fun FirstBaggageInfo(
    state: String
) {
    if(state != "BaggageFromMore") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Baggage",
                fontSize = 22.sp,
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
            Icon(
                Icons.Filled.Luggage,
                contentDescription = "Luggage",
                modifier = Modifier.padding(start = 5.dp, end = 45.dp, top = 15.dp),
                tint = Color(0xFF023E8A)
            )
        }
    }
    Image(
        painter = painterResource(id = R.drawable.baggage),
        contentDescription = "baggage",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f)
            .padding(start = 10.dp, end = 10.dp)
    )
    Row {
        Icon(
            Icons.Filled.WarningAmber,
            contentDescription = "Warning",
            modifier = Modifier.padding(start = 10.dp, top = 15.dp),
            tint = Color(0xFF023E8A)
        )
        Text(
            text = "Αll passengers are entitled to a free 8kg baggage in the aircraft cabin.",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 10.dp, top = 5.dp, end = 10.dp),
            color = Color(0xFF023E8A),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            )
        )
    }
    Text(
        text = "Outbound",
        fontSize = 22.sp,
        modifier = Modifier.padding(start = 10.dp, top = 5.dp),
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