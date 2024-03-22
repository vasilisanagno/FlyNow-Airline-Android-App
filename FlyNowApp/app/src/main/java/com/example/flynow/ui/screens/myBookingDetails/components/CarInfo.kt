package com.example.flynow.ui.screens.myBookingDetails.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel

//Component that shows car information for a booking
@Composable
fun CarInfo(
    sharedViewModel: SharedViewModel
){
    Row {
        Icon(
            Icons.Filled.DirectionsCar,
            contentDescription = "car",
            tint = Color(0xFF023E8A),
            modifier = Modifier.padding(start = 10.dp, top = 12.dp)
        )
        Text(
            text = "Car",
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
    Column {
        if(sharedViewModel.carsMyBooking.size == 0) {
            Text(
                text = "No Cars Selected",
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
        }
        else {
            for (index in 0 until sharedViewModel.carsMyBooking.size) {
                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Card(
                        modifier = Modifier
                            .padding(
                                top = if (index == 0)
                                    20.dp else 30.dp,
                                start = 5.dp,
                                end = 5.dp,
                                bottom = 10.dp
                            )
                            .width(350.dp)
                            .height(250.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = ShapeDefaults.Small,
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Image(
                                    bitmap = sharedViewModel.carsMyBooking[index]!!.carImage.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.size(160.dp, 160.dp)
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = sharedViewModel.carsMyBooking[index]!!.model,
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        modifier = Modifier.padding(
                                            top = 10.dp,
                                            bottom = 10.dp,
                                            start = 20.dp
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF023E8A)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Outlined.Person,
                                            contentDescription = "person",
                                            tint = Color(0xFF023E8A),
                                            modifier = Modifier.padding(start = 10.dp)
                                        )
                                        Text(
                                            text = "x5",
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            color = Color(0xFF023E8A),
                                            modifier = Modifier.padding(start = 2.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Icon(
                                            Icons.Outlined.Luggage,
                                            contentDescription = "baggage",
                                            tint = Color(0xFF023E8A),
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                        Text(
                                            text = "x2",
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            color = Color(0xFF023E8A),
                                            modifier = Modifier.padding(start = 2.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Column {
                                Text(
                                    text = sharedViewModel.carsMyBooking[index]!!.company,
                                    fontSize = 22.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.roboto
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF023E8A),
                                    textDecoration = TextDecoration.Underline
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(end = 10.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                        CarLocation(sharedViewModel = sharedViewModel, index = index)

                                        CarRentDate(sharedViewModel = sharedViewModel, index = index, action = "Pick Up")

                                        CarRentDate(sharedViewModel = sharedViewModel, index = index, action = "Return")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        thickness = 1.dp,
        color = Color(0xFF023E8A)
    )
}