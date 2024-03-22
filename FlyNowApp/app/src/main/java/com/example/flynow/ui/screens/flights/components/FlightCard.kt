package com.example.flynow.ui.screens.flights.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.model.DirectFlight
import com.example.flynow.model.OneStopFlight
import com.example.flynow.model.ReservationType
import com.example.flynow.ui.screens.flights.FlightsViewModel

//component that shows the flight card with all the necessary info about the flights
@Composable
fun FlightCard(
    directFlight: DirectFlight?,
    oneStopFlight: OneStopFlight?,
    flightsViewModel: FlightsViewModel,
    index: Int,
    listOfClassButtons: MutableList<ReservationType>,
    topPadding: Boolean,
    bottomPadding: Boolean,
    type: String,
    totalHours: Int,
    totalMinutes: Int,
    showDialog: MutableState<Boolean>
) {
    Card(
        modifier = Modifier
            .padding(
                top = if ((index == 0 && directFlight!=null)||(topPadding && oneStopFlight!=null))
                    20.dp else 30.dp,
                start = 20.dp,
                end = 20.dp,
                bottom = if (bottomPadding) 80.dp else 0.dp
            )
            .width(350.dp)
            .height(285.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = ShapeDefaults.Small,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .height(150.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = directFlight?.flightId ?: oneStopFlight!!.firstFlightId,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF029fff),
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                )
                if (oneStopFlight != null) {
                    Icon(
                        Icons.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                    )
                    Text(
                        text = oneStopFlight.secondFlightId,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF029fff),
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = directFlight?.departureTime ?: oneStopFlight!!.firstDepartureTime,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 10.dp, top = 18.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = directFlight?.departureAirport ?: oneStopFlight!!.firstDepartureAirport,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 15.dp, top = 10.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = directFlight?.flightDuration ?:
                        if(totalHours!=0&&totalMinutes!=0) "${totalHours}h ${totalMinutes}min"
                        else if(totalHours!=0) "${totalHours}h"
                        else "${totalMinutes}min",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF023E8A),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Box(contentAlignment = Alignment.Center)
                    {
                        Divider(
                            modifier = Modifier.width(180.dp),
                            color = Color(0xFF023E8A),
                            thickness = 0.5.dp
                        )
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor =
                                if(directFlight!=null) Color(0xFF029fff)
                                else Color(0xFF0070f0)
                            ),
                            shape = ShapeDefaults.ExtraLarge,
                            border = BorderStroke(width = 0.5.dp, color = Color(0xFF023E8A)),
                            modifier = Modifier
                                .width(70.dp)
                                .height(20.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center) {
                                Text(
                                    text = if(directFlight!=null) "nonstop" else "1 stop",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Button(
                        onClick = { showDialog.value = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(5.dp)
                    ) {
                        Text(
                            text = "Flight details",
                            fontSize = 12.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF028FFF),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = directFlight?.arrivalTime ?: oneStopFlight!!.secondArrivalTime,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF023E8A),
                        modifier = Modifier.padding(end = 10.dp, top = 18.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = directFlight?.arrivalAirport ?: oneStopFlight!!.secondArrivalAirport,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF023E8A),
                        modifier = Modifier.padding(end = 15.dp, top = 10.dp)
                    )
                }
            }
            FlightClassButtons(
                directFlight = directFlight,
                oneStopFlight = oneStopFlight,
                flightsViewModel = flightsViewModel,
                listOfClassButtons = listOfClassButtons,
                index = index,
                type = type
            )
        }
    }
}