package com.example.flynow.ui.screens.flights.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
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
import com.example.flynow.model.DirectFlight
import com.example.flynow.model.OneStopFlight

//component that shows the info in flight details about the time(departure)
@Composable
fun DepartureArrivalInfo(
    directFlight: DirectFlight?,
    oneStopFlight: OneStopFlight?,
    i: Int
) {
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = directFlight?.departureTime
                        ?: if(i==0) oneStopFlight!!.firstDepartureTime
                        else oneStopFlight!!.secondDepartureTime,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, end = 20.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text =
                    if(directFlight!=null) "${directFlight.departureCity} (${directFlight.departureAirport})"
                    else if(i==0) "${oneStopFlight!!.firstDepartureCity} (${oneStopFlight.firstDepartureAirport})"
                    else "${oneStopFlight!!.secondDepartureCity} (${oneStopFlight.secondDepartureAirport})",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                    fontWeight = FontWeight.Bold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Icon(
                        Icons.Outlined.ArrowDownward,
                        contentDescription = null,
                        tint = Color(0xFF023E8A)
                    )
                    Icon(
                        Icons.Outlined.ArrowDownward,
                        contentDescription = null,
                        tint = Color(0xFF023E8A)
                    )
                    Icon(
                        Icons.Outlined.ArrowDownward,
                        contentDescription = null,
                        tint = Color(0xFF023E8A)
                    )
                }
                Row(modifier = Modifier.padding(start = 30.dp)) {
                    Text(
                        text = "Flight duration: ",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Text(
                        text = directFlight?.flightDuration ?:
                        if(i==0) oneStopFlight!!.firstFlightDuration
                        else oneStopFlight!!.secondFlightDuration,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        modifier = Modifier.padding(top = 10.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = directFlight?.arrivalTime ?:
                    if(i==0) oneStopFlight!!.firstArrivalTime
                    else oneStopFlight!!.secondArrivalTime,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    modifier = Modifier.padding(top = 10.dp, end = 20.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text =
                    if(directFlight!=null) "${directFlight.arrivalCity} (${directFlight.arrivalAirport})"
                    else if(i==0) "${oneStopFlight!!.firstArrivalCity} (${oneStopFlight.firstArrivalAirport})"
                    else "${oneStopFlight!!.secondArrivalCity} (${oneStopFlight.secondArrivalAirport})",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    modifier = Modifier.padding(top = 10.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}