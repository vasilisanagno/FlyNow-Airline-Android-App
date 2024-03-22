package com.example.flynow.ui.screens.flights.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.flights.FlightsViewModel

//component that shows the first info and has some buttons about sorting before the flight cards
@Composable
fun FirstFlightInfo(
    directFlight: DirectFlight?,
    oneStopFlight: OneStopFlight?,
    flightsViewModel: FlightsViewModel,
    sharedViewModel: SharedViewModel,
    returnOrNot: Boolean
) {
    //info about the outbound flights and two buttons with the sorting
    if(returnOrNot) {
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
            color = Color(0xFF023E8A)
        )
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if(!returnOrNot) "Outbound" else "Inbound",
            fontSize = 20.sp,
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color(0xFF023E8A),
            modifier = Modifier.padding(start = 10.dp, top = 20.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = directFlight?.flightDate ?: oneStopFlight!!.firstFlightDate,
            fontSize = 20.sp,
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color(0xFF023E8A),
            modifier = Modifier.padding(start = 35.dp, top = 20.dp),
            fontWeight = FontWeight.Bold
        )
    }
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = directFlight?.departureCity ?: oneStopFlight!!.firstDepartureCity,
            fontSize = 18.sp,
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color(0xFF023E8A),
            modifier = Modifier.padding(start = 15.dp, top = 10.dp),
            fontWeight = FontWeight.Bold
        )
        Icon(
            Icons.Outlined.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF023E8A),
            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
        )
        Text(
            text = directFlight?.arrivalCity ?: oneStopFlight!!.secondArrivalCity,
            fontSize = 18.sp,
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color(0xFF023E8A),
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            fontWeight = FontWeight.Bold
        )
    }
    //sorting buttons by price and by departure time
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        Button(
            onClick = {
                if(!returnOrNot) {
                    flightsViewModel.sortPrice = !flightsViewModel.sortPrice
                    flightsViewModel.sortDepartureTime = false
                }
                else {
                    flightsViewModel.sortPriceReturn = !flightsViewModel.sortPriceReturn
                    flightsViewModel.sortDepartureTimeReturn = false
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor =
                if ((!returnOrNot&&flightsViewModel.sortPrice)||
                    (returnOrNot&&flightsViewModel.sortPriceReturn))
                    Color(0xFF023E8A)
                else
                    Color.White,
                contentColor =
                if((!returnOrNot&&flightsViewModel.sortPrice)||
                    (returnOrNot&&flightsViewModel.sortPriceReturn))
                    Color.White
                else
                    Color(0xFF023E8A)
            ),
            border = BorderStroke(width = 1.dp, color = Color(0xFF023E8A)),
            modifier = Modifier.padding(top = 10.dp,end = 5.dp),
            contentPadding = PaddingValues(10.dp),
            enabled = if(!returnOrNot) sharedViewModel.totalPriceOneWay == 0.0 else sharedViewModel.totalPriceReturn == 0.0
        ) {
            Text(text = if((!returnOrNot&&!flightsViewModel.sortPrice)||
                (returnOrNot&&!flightsViewModel.sortPriceReturn))
                "Sorting by price" else "Sorted by price",
                fontSize = 14.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
        }
        Button(
            onClick = {
                if(!returnOrNot) {
                    flightsViewModel.sortDepartureTime = !flightsViewModel.sortDepartureTime
                    flightsViewModel.sortPrice = false
                }
                else {
                    flightsViewModel.sortDepartureTimeReturn = !flightsViewModel.sortDepartureTimeReturn
                    flightsViewModel.sortPriceReturn = false
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor =
                if ((!returnOrNot&&flightsViewModel.sortDepartureTime)||
                    (returnOrNot&&flightsViewModel.sortDepartureTimeReturn))
                    Color(0xFF023E8A)
                else
                    Color.White,
                contentColor =
                if((!returnOrNot&&flightsViewModel.sortDepartureTime)||
                    (returnOrNot&&flightsViewModel.sortDepartureTimeReturn))
                    Color.White
                else
                    Color(0xFF023E8A)
            ),
            border = BorderStroke(width = 1.dp, color = Color(0xFF023E8A)),
            modifier = Modifier.padding(top = 10.dp,start = 5.dp),
            contentPadding = PaddingValues(10.dp),
            enabled = if(!returnOrNot) sharedViewModel.totalPriceOneWay == 0.0 else sharedViewModel.totalPriceReturn == 0.0
        ) {
            Text(text = if((!returnOrNot&&!flightsViewModel.sortDepartureTime)||
                (returnOrNot&&!flightsViewModel.sortDepartureTimeReturn)) "Sorting by departure time" else "Sorted by departure time",
                fontSize = 14.sp,
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