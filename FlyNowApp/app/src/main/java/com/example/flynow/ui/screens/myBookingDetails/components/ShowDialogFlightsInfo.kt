package com.example.flynow.ui.screens.myBookingDetails.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.myBookingDetails.MyBookingDetailsViewModel

//alert dialog for the more details in the flights
//and takes as parameters the info that must show in the dialog
//like duration and flightsMyBooking list that has all the information about flights
@Composable
fun ShowDialogFlightsInfo(
    myBookingDetailsViewModel: MyBookingDetailsViewModel,
    sharedViewModel: SharedViewModel,
    duration: String
){
    val repeatTimes = remember {
        mutableIntStateOf(0)
    }

    if(myBookingDetailsViewModel.outboundFlight){
        if(sharedViewModel.outboundDirect){
            repeatTimes.intValue = 1
        }
        else{
            repeatTimes.intValue = 2
        }
    }
    else{
        if(sharedViewModel.inboundDirect){
            repeatTimes.intValue = 1
        }
        else{
            repeatTimes.intValue = 2
        }
    }

    Box(
        contentAlignment = Alignment.Center
    ){
        AlertDialog(
            onDismissRequest = { myBookingDetailsViewModel.showDialogFlights = false },
            title = {
                Text(
                    text = "More Details",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 10.dp),
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
            },
            text = {
                LazyColumn {
                    items(repeatTimes.intValue) {index->
                        Log.d("index", index.toString())

                        if(index==0) {
                            Text(
                                if (myBookingDetailsViewModel.outboundFlight) "Outbound" else "Inbound",
                                fontSize = 20.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                ),
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                Text(
                                    "Total time: ",
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
                                    text = if (myBookingDetailsViewModel.outboundFlight)
                                        "${duration}, " + if (sharedViewModel.outboundDirect) "nonstop" else "one-stop"
                                    else
                                        "${duration}, " + if (sharedViewModel.inboundDirect) "nonstop" else "one-stop",
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
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            thickness = 1.dp,
                            color = Color(0xFF023E8A)
                        )
                        if((myBookingDetailsViewModel.outboundFlight && !sharedViewModel.outboundDirect) || (!myBookingDetailsViewModel.outboundFlight && !sharedViewModel.inboundDirect)) {
                            Text(
                                text = if (index == 0) "1st Flight" else "2nd Flight",
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
                        Row {
                            Text(
                                "Flight date: ",
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
                                text =
                                    if(myBookingDetailsViewModel.outboundFlight)
                                        "${sharedViewModel.flightsMyBooking[index]?.flightDate}"
                                    else if(!myBookingDetailsViewModel.outboundFlight && sharedViewModel.outboundDirect)
                                        "${sharedViewModel.flightsMyBooking[index+1]?.flightDate}"
                                    else
                                        "${sharedViewModel.flightsMyBooking[index+2]?.flightDate}",
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
                        Text(
                            text =
                                if(myBookingDetailsViewModel.outboundFlight)
                                    "${sharedViewModel.flightsMyBooking[index]?.departureTime}     ${sharedViewModel.flightsMyBooking[index]?.departureCity} (${sharedViewModel.flightsMyBooking[index]?.departureAirp})"
                                else if(!myBookingDetailsViewModel.outboundFlight&& sharedViewModel.outboundDirect)
                                    "${sharedViewModel.flightsMyBooking[index+1]?.departureTime}     ${sharedViewModel.flightsMyBooking[index+1]?.departureCity} (${sharedViewModel.flightsMyBooking[index+1]?.departureAirp})"
                                else
                                    "${sharedViewModel.flightsMyBooking[index+2]?.departureTime}     ${sharedViewModel.flightsMyBooking[index+2]?.departureCity} (${sharedViewModel.flightsMyBooking[index+2]?.departureAirp})",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 10.dp),
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
                                    text =
                                        if(myBookingDetailsViewModel.outboundFlight)
                                            "${sharedViewModel.flightsMyBooking[index]?.flightDuration}"
                                        else if(!myBookingDetailsViewModel.outboundFlight && sharedViewModel.outboundDirect)
                                            "${sharedViewModel.flightsMyBooking[index+1]?.flightDuration}"
                                        else
                                            "${sharedViewModel.flightsMyBooking[index+2]?.flightDuration}",
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
                        Text(
                            text =
                                if(myBookingDetailsViewModel.outboundFlight)
                                    "${sharedViewModel.flightsMyBooking[index]?.arrivalTime}     ${sharedViewModel.flightsMyBooking[index]?.arrivalCity} (${sharedViewModel.flightsMyBooking[index]?.arrivalAirp})"
                                else if(!myBookingDetailsViewModel.outboundFlight && sharedViewModel.outboundDirect)
                                    "${sharedViewModel.flightsMyBooking[index+1]?.arrivalTime}     ${sharedViewModel.flightsMyBooking[index+1]?.arrivalCity} (${sharedViewModel.flightsMyBooking[index+1]?.arrivalAirp})"
                                else
                                    "${sharedViewModel.flightsMyBooking[index+2]?.arrivalTime}     ${sharedViewModel.flightsMyBooking[index+2]?.arrivalCity} (${sharedViewModel.flightsMyBooking[index+2]?.arrivalAirp})",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 10.dp),
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
                        Row{
                            Text(
                                text = "Flight number: ",
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
                                text =
                                    if (myBookingDetailsViewModel.outboundFlight)
                                        "${sharedViewModel.flightsMyBooking[index]?.flightId}"
                                    else if (!myBookingDetailsViewModel.outboundFlight && sharedViewModel.outboundDirect)
                                        "${sharedViewModel.flightsMyBooking[index + 1]?.flightId}"
                                    else
                                        "${sharedViewModel.flightsMyBooking[index + 2]?.flightId}",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 10.dp),
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
                        Row{
                            Text(
                                text = "Airplane model: ",
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
                                text =
                                    if (myBookingDetailsViewModel.outboundFlight)
                                        "${sharedViewModel.flightsMyBooking[index]?.airplaneModel}"
                                    else if (!myBookingDetailsViewModel.outboundFlight && sharedViewModel.outboundDirect)
                                        "${sharedViewModel.flightsMyBooking[index + 1]?.airplaneModel}"
                                    else
                                        "${sharedViewModel.flightsMyBooking[index + 2]?.airplaneModel}",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 10.dp),
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
                        Row{
                            Text(
                                text = "Booking class: ",
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
                                text =
                                    if (myBookingDetailsViewModel.outboundFlight)
                                        "${sharedViewModel.flightsMyBooking[index]?.classType}"
                                    else if (!myBookingDetailsViewModel.outboundFlight && sharedViewModel.outboundDirect)
                                        "${sharedViewModel.flightsMyBooking[index + 1]?.classType}"
                                    else
                                        "${sharedViewModel.flightsMyBooking[index + 2]?.classType}",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 10.dp),
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        myBookingDetailsViewModel.showDialogFlights = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF023E8A)
                    ),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        "OK",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        )
                    )
                }
            },
            containerColor = Color(0xFFEBF2FA),
            textContentColor = Color(0xFF023E8A),
            titleContentColor = Color(0xFF023E8A),
            tonalElevation = 30.dp,
            modifier = Modifier
                .height(400.dp)
                .width(400.dp),
            properties = DialogProperties(dismissOnClickOutside = true)
        )
    }
}
