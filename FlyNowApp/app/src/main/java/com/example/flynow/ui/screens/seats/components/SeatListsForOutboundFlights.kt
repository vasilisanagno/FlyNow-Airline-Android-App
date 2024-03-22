package com.example.flynow.ui.screens.seats.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.seats.SeatsViewModel

//component that shows the seat lists for outbound flights
//direct or one stop flights
@Composable
fun SeatListsForOutboundFlights(
    sharedViewModel: SharedViewModel,
    seatsViewModel: SeatsViewModel,
    index: Int
) {
    if (index == 0) {
        Image(
            painter = painterResource(id = R.drawable.seats),
            contentDescription = "airplaneseat",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f)
                .padding(start = 10.dp, end = 10.dp)
        )
        Text(
            "Select a seat from 1x to 30x where x: A,B,C,D,E,F. " +
                    "There is no charge for seat selection.",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 10.dp, top = 1.dp),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color(0xFF023E8A)
        )
        if(sharedViewModel.bookingFailed) {
            Text(
                "Your reservation failed because some of the seats you selected are already taken!",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 10.dp, top = 1.dp),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                color = Color.Red
            )
        }
        Text(
            "Outbound",
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
        if(sharedViewModel.selectedFlightOutbound == 0) {
            SeatsDropdownMenu(
                sharedViewModel = sharedViewModel,
                seatsViewModel = seatsViewModel,
                seats = seatsViewModel.seatListOutboundDirect,
                index = index,
                column = 0,
                isClickedSeat = seatsViewModel.isClickedSeat[0]
            )
        }
        else {
            SeatsDropdownMenu(
                sharedViewModel = sharedViewModel,
                seatsViewModel = seatsViewModel,
                seats = seatsViewModel.seatListOutboundOneStop[0],
                index = index,
                column = 0,
                isClickedSeat = seatsViewModel.isClickedSeat[0]
            )
        }
        Text(
            text = sharedViewModel.selectedFlights[0].departureCity.value,
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
            text = sharedViewModel.selectedFlights[0].arrivalCity.value,
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
    if(sharedViewModel.selectedFlightOutbound == 1) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            SeatsDropdownMenu(
                sharedViewModel = sharedViewModel,
                seatsViewModel = seatsViewModel,
                seats = seatsViewModel.seatListOutboundOneStop[1],
                index = index,
                column = 1,
                isClickedSeat = seatsViewModel.isClickedSeat[1]
            )
            Text(
                text = sharedViewModel.selectedFlights[1].departureCity.value,
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
                text = sharedViewModel.selectedFlights[1].arrivalCity.value,
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
    }
    if (index < sharedViewModel.passengersCounter - 1 || sharedViewModel.page == 1) {
        if(sharedViewModel.page == 1 && index==sharedViewModel.passengersCounter-1) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 10.dp),
                thickness = 2.dp,
                color = Color(0xFF023E8A)
            )
        }
        else {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 10.dp),
                thickness = 1.dp,
                color = Color(0xFF023E8A)
            )
        }
    } else {
        Spacer(modifier = Modifier.padding(bottom = 80.dp))
    }
}