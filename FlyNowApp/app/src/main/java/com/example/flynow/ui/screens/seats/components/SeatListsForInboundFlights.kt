package com.example.flynow.ui.screens.seats.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.example.flynow.ui.screens.seats.SeatsViewModel

//component that shows the seat lists for inbound flights
//direct or one stop flights
@Composable
fun SeatListsForInboundFlights(
    sharedViewModel: SharedViewModel,
    seatsViewModel: SeatsViewModel,
    index: Int
) {
    val indexNew: Int = index + sharedViewModel.passengersCounter
    if (index == 0) {
        Text(
            "Inbound",
            fontSize = 22.sp,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color(0xFF023E8A),
            fontWeight = FontWeight.Bold
        )
    }
    Text(
        text =
        if (sharedViewModel.passengers[index].gender.value == "Female")
            "Mrs ${sharedViewModel.passengers[index].firstname.value} ${sharedViewModel.passengers[index].lastname.value}"
        else
            "Mr ${sharedViewModel.passengers[index].firstname.value} ${sharedViewModel.passengers[index].lastname.value}",
        fontSize = 20.sp,
        modifier = Modifier.padding(
            start = 10.dp,
            top = if (index == 0) 10.dp else 5.dp
        ),
        fontFamily = FontFamily(
            fonts = listOf(
                Font(
                    resId = R.font.opensans
                )
            )
        ),
        color = Color(0xFF023E8A),
        fontWeight = FontWeight.Bold
    )
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        if(sharedViewModel.selectedFlightInbound == 0) {
            SeatsDropdownMenu(
                sharedViewModel = sharedViewModel,
                seatsViewModel = seatsViewModel,
                seats = seatsViewModel.seatListInboundDirect,
                index = indexNew,
                column = 0,
                isClickedSeat =
                if(sharedViewModel.selectedFlightOutbound == 0)
                    seatsViewModel.isClickedSeat[1]
                else
                    seatsViewModel.isClickedSeat[2]
            )
        }
        else {
            SeatsDropdownMenu(
                sharedViewModel = sharedViewModel,
                seatsViewModel = seatsViewModel,
                seats = seatsViewModel.seatListInboundOneStop[0],
                index = indexNew,
                column = 0,
                isClickedSeat =
                if(sharedViewModel.selectedFlightOutbound == 0)
                    seatsViewModel.isClickedSeat[1]
                else
                    seatsViewModel.isClickedSeat[2]
            )
        }
        Text(
            text =
            if(sharedViewModel.selectedFlightOutbound == 0)
                sharedViewModel.selectedFlights[1].departureCity.value
            else
                sharedViewModel.selectedFlights[2].departureCity.value,
            fontSize = 16.sp,
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color(0xFF023E8A),
            modifier = Modifier.padding(start = 5.dp, top = 10.dp),
            fontWeight = FontWeight.Bold
        )
        Icon(
            Icons.Outlined.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF023E8A),
            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
        )
        Text(
            text =
            if(sharedViewModel.selectedFlightOutbound == 0)
                sharedViewModel.selectedFlights[1].arrivalCity.value
            else
                sharedViewModel.selectedFlights[2].arrivalCity.value,
            fontSize = 16.sp,
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
    if(sharedViewModel.selectedFlightInbound == 1) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            SeatsDropdownMenu(
                sharedViewModel = sharedViewModel,
                seatsViewModel = seatsViewModel,
                seats = seatsViewModel.seatListInboundOneStop[1],
                index = indexNew,
                column = 1,
                isClickedSeat =
                if (sharedViewModel.selectedFlightOutbound == 0)
                    seatsViewModel.isClickedSeat[2]
                else
                    seatsViewModel.isClickedSeat[3]
            )
            Text(
                text =
                if (sharedViewModel.selectedFlightOutbound == 0)
                    sharedViewModel.selectedFlights[2].departureCity.value
                else
                    sharedViewModel.selectedFlights[3].departureCity.value,
                fontSize = 16.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                color = Color(0xFF023E8A),
                modifier = Modifier.padding(
                    start = 5.dp,
                    top = 10.dp
                ),
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.Outlined.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF023E8A),
                modifier = Modifier.padding(
                    start = 10.dp,
                    top = 10.dp
                )
            )
            Text(
                text =
                if (sharedViewModel.selectedFlightOutbound == 0)
                    sharedViewModel.selectedFlights[2].arrivalCity.value
                else
                    sharedViewModel.selectedFlights[3].arrivalCity.value,
                fontSize = 16.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                color = Color(0xFF023E8A),
                modifier = Modifier.padding(
                    start = 10.dp,
                    top = 10.dp
                ),
                fontWeight = FontWeight.Bold
            )
        }
    }
    if (index < sharedViewModel.passengersCounter - 1) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp),
            thickness = 1.dp,
            color = Color(0xFF023E8A)
        )
    } else {
        Spacer(modifier = Modifier.padding(bottom = 80.dp))
    }
}