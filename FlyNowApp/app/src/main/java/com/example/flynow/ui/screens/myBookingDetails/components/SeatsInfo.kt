package com.example.flynow.ui.screens.myBookingDetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
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

//Component that shows seats information and if the flight is checked-in
@Composable
fun SeatsInfo(
    sharedViewModel: SharedViewModel,
    flightType: String
){
    if(flightType == "Outbound" || flightType == "Checkin"){
        Row {
            Icon(
                Icons.Filled.AirlineSeatReclineNormal,
                contentDescription = "seats",
                tint = Color(0xFF023E8A),
                modifier = Modifier.padding(start = 10.dp, top = 12.5.dp)
            )
            Text(
                text = "Seats",
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

    if(flightType != "Checkin"){
        Text(
            text = flightType,
            fontSize = 20.sp,
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
    }


    Column {
        for (index in 0 until if(flightType == "Checkin") sharedViewModel.numOfPassengersCheckIn else sharedViewModel.numOfPassengers) {
            Row {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "passenger",
                    tint = Color(0xFF0077FF),
                    modifier = Modifier.padding(top = 11.dp, start = 10.dp)
                )
                Text(
                    text =
                        if(flightType == "Outbound"){
                            if (sharedViewModel.baggageAndSeatMyBooking[index]?.gender == "Female")
                                "Mrs ${sharedViewModel.baggageAndSeatMyBooking[index]?.firstname} ${sharedViewModel.baggageAndSeatMyBooking[index]?.lastname}"
                            else
                                "Mr ${sharedViewModel.baggageAndSeatMyBooking[index]?.firstname} ${sharedViewModel.baggageAndSeatMyBooking[index]?.lastname}"
                        }
                        else if(flightType == "Inbound"){
                            if (sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.gender == "Female")
                                "Mrs ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.firstname} ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.lastname}"
                            else
                                "Mr ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.firstname} ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.lastname}"
                            }
                    else{
                            if (sharedViewModel.baggageAndSeatCheckIn[index]?.gender == "Female")
                                "Mrs ${sharedViewModel.baggageAndSeatCheckIn[index]?.firstname} ${sharedViewModel.baggageAndSeatCheckIn[index]?.lastname}"
                            else
                                "Mr ${sharedViewModel.baggageAndSeatCheckIn[index]?.firstname} ${sharedViewModel.baggageAndSeatCheckIn[index]?.lastname}"
                        },
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 5.dp, top = 10.dp),
                    color = Color(0xFF0077FF),
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
            Text(
                text =
                    if(flightType == "Outbound"){
                        if(sharedViewModel.outboundDirect)
                            "${sharedViewModel.baggageAndSeatMyBooking[index]?.departurecity} to ${sharedViewModel.baggageAndSeatMyBooking[index]?.arrivalcity}\n" +
                                    "Seat Number: ${sharedViewModel.baggageAndSeatMyBooking[index]?.seatnumber}"
                        else
                            "${sharedViewModel.baggageAndSeatMyBooking[index]?.departurecity} to ${sharedViewModel.baggageAndSeatMyBooking[index]?.arrivalcity}\n" +
                                    "Seat Number: ${sharedViewModel.baggageAndSeatMyBooking[index]?.seatnumber}\n\n" +
                                    "${sharedViewModel.baggageAndSeatMyBooking[index + sharedViewModel.numOfPassengers]?.departurecity} to ${sharedViewModel.baggageAndSeatMyBooking[index + sharedViewModel.numOfPassengers]?.arrivalcity}\n" +
                                    "Seat Number: ${sharedViewModel.baggageAndSeatMyBooking[index + sharedViewModel.numOfPassengers]?.seatnumber}"
                    }
                    else if(flightType == "Inbound"){
                        if (sharedViewModel.outboundDirect && sharedViewModel.inboundDirect)//1->1
                        "${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.departurecity} to ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.arrivalcity}\n" +
                                "Seat Number: ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.seatnumber}"
                        else if (sharedViewModel.outboundDirect && !sharedViewModel.inboundDirect)//1->2
                            "${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.departurecity} to ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.arrivalcity}\n" +
                                    "Seat Number: ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.seatnumber}\n\n" +
                                    "${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index]?.departurecity} to ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index]?.arrivalcity}\n" +
                                    "Seat Number: ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index]?.seatnumber}"
                        else if (!sharedViewModel.outboundDirect && sharedViewModel.inboundDirect)//2->1
                            "${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index]?.departurecity} to ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index]?.arrivalcity}\n" +
                                    "Seat Number: ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index]?.seatnumber}"
                        else//2->2
                            "${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index]?.departurecity} to ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index]?.arrivalcity}\n" +
                                    "Seat Number: ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index]?.seatnumber}\n\n" +
                                    "${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index + sharedViewModel.numOfPassengers]?.departurecity} to ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index + sharedViewModel.numOfPassengers]?.arrivalcity}\n" +
                                    "Seat Number: ${sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index + sharedViewModel.numOfPassengers]?.seatnumber}"
                        }
                    else{
                        if (sharedViewModel.directFlight)
                            "${sharedViewModel.baggageAndSeatCheckIn[index]?.departurecity} to ${sharedViewModel.baggageAndSeatCheckIn[index]?.arrivalcity}\n"+
                                    "Seat Number: ${sharedViewModel.baggageAndSeatCheckIn[index]?.seatnumber}"
                        else
                            "${sharedViewModel.baggageAndSeatCheckIn[index]?.departurecity} to ${sharedViewModel.baggageAndSeatCheckIn[index]?.arrivalcity}\n"+
                                    "Seat Number: ${sharedViewModel.baggageAndSeatCheckIn[index]?.seatnumber}\n\n"+
                                    "${sharedViewModel.baggageAndSeatCheckIn[index+sharedViewModel.numOfPassengersCheckIn]?.departurecity} to ${sharedViewModel.baggageAndSeatCheckIn[index+sharedViewModel.numOfPassengersCheckIn]?.arrivalcity}\n"+
                                    "Seat Number: ${sharedViewModel.baggageAndSeatCheckIn[index+sharedViewModel.numOfPassengersCheckIn]?.seatnumber}"
                        },
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
            if(flightType != "Checkin"){
                Text(
                    text =
                    if(flightType == "Outbound"){
                        if(sharedViewModel.baggageAndSeatMyBooking[index]?.checkin == true)
                            "Checked-in\n"
                        else "Not checked-in\n"
                    }
                    else{
                        if((sharedViewModel.outboundDirect && sharedViewModel.inboundDirect) || (sharedViewModel.outboundDirect && !sharedViewModel.inboundDirect))
                            if(sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.checkin == true)
                                "Checked-in\n"
                            else
                                "Not checked-in\n"
                        else
                            if(sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index]?.checkin == true)
                                "Checked-in\n"
                            else
                                "Not checked-in\n"
                    },
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    color =
                    if(flightType == "Outbound"){
                        if(sharedViewModel.baggageAndSeatMyBooking[index]?.checkin == true)
                            Color(0xFF023E8A)
                        else Color.Red
                    }
                    else{
                        if((sharedViewModel.outboundDirect && sharedViewModel.inboundDirect) || (sharedViewModel.outboundDirect && !sharedViewModel.inboundDirect))
                            if(sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers + index]?.checkin == true)
                                Color(0xFF023E8A)
                            else
                                Color.Red
                        else
                            if(sharedViewModel.baggageAndSeatMyBooking[sharedViewModel.numOfPassengers * 2 + index]?.checkin == true)
                                Color(0xFF023E8A)
                            else
                                Color.Red
                    },
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

    if(flightType == "Inbound" || (flightType == "Outbound" && sharedViewModel.oneWay) || flightType == "Checkin"){
        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            thickness = 1.dp,
            color = Color(0xFF023E8A)
        )
    }
}
