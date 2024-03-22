package com.example.flynow.ui.screens.myBookingDetails.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.checkInDetails.CheckInDetailsViewModel
import com.example.flynow.ui.screens.myBookingDetails.MyBookingDetailsViewModel
import com.example.flynow.utils.Converters
import com.example.flynow.utils.Time

//component that shows the information about the flights (outbound and inbound) and contains a
// button "More details" that shows a dialog with more information
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlightInfo(
    sharedViewModel: SharedViewModel,
    myBookingDetailsViewModel: MyBookingDetailsViewModel,
    flightType: String,
    icon: ImageVector,
    checkInDetailsViewModel: CheckInDetailsViewModel
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Icon(
                icon,
                contentDescription = flightType,
                tint = Color(0xFF023E8A),
                modifier = Modifier.padding(start = 10.dp, top = 12.dp)
            )
            Text(
                text = flightType,
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

        if(flightType == "Outbound" || !sharedViewModel.oneWay){
            Button(
                onClick = {
                    if(flightType == "Flight"){
                        checkInDetailsViewModel.showDialogFlights = true
                    }
                    else{
                        myBookingDetailsViewModel.showDialogFlights = true
                        if (flightType == "Outbound") {
                            myBookingDetailsViewModel.outboundFlight = true
                        }
                        else if(flightType == "Inbound"){
                            myBookingDetailsViewModel.outboundFlight = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(5.dp),
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text(
                    text = "More details",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF023FCC),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }

    if (flightType == "Inbound" && sharedViewModel.oneWay) {
        Text(
            text = "No Inbound flights",
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
    else{
        when (flightType) {
            "Outbound" -> {
                //converts the flight date to day name and all the others like Saturday 19 March 2024
                myBookingDetailsViewModel.dateInNums =
                    sharedViewModel.flightsMyBooking[0]?.flightDate.toString()
            }
            "Inbound" -> {
                //converts the flight date to day name and all the others like Saturday 19 March 2024
                myBookingDetailsViewModel.dateInNums =
                    sharedViewModel.flightsMyBooking[myBookingDetailsViewModel.flightIndex + 1]?.flightDate.toString()
            }
            else -> {
                myBookingDetailsViewModel.dateInNums = sharedViewModel.flightsCheckIn[0]?.flightDate.toString()
            }
        }
        myBookingDetailsViewModel.dateInWords =
            Converters.dateToString(myBookingDetailsViewModel.dateInNums)
        Text(
            text = myBookingDetailsViewModel.dateInWords,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
        if ((flightType == "Outbound" && !sharedViewModel.outboundDirect) || (!sharedViewModel.directFlight && flightType == "Flight")) {
            myBookingDetailsViewModel.flightIndex = 1
        }
        Text(
            text =
            when (flightType) {
                "Outbound" -> {
                    "${sharedViewModel.flightsMyBooking[0]?.departureTime} ${sharedViewModel.flightsMyBooking[0]?.departureCity} - " +
                            if (sharedViewModel.outboundDirect)
                                "${sharedViewModel.flightsMyBooking[0]?.arrivalTime} ${sharedViewModel.flightsMyBooking[0]?.arrivalCity}"
                            else
                                "${sharedViewModel.flightsMyBooking[1]?.arrivalTime} ${sharedViewModel.flightsMyBooking[1]?.arrivalCity}"
                }
                "Inbound" -> {
                    "${sharedViewModel.flightsMyBooking[myBookingDetailsViewModel.flightIndex + 1]?.departureTime} ${sharedViewModel.flightsMyBooking[myBookingDetailsViewModel.flightIndex + 1]?.departureCity} - " +
                            if (sharedViewModel.inboundDirect)
                                "${sharedViewModel.flightsMyBooking[myBookingDetailsViewModel.flightIndex + 1]?.arrivalTime} ${sharedViewModel.flightsMyBooking[myBookingDetailsViewModel.flightIndex + 1]?.arrivalCity}"
                            else
                                "${sharedViewModel.flightsMyBooking[myBookingDetailsViewModel.flightIndex + 2]?.arrivalTime}  ${sharedViewModel.flightsMyBooking[myBookingDetailsViewModel.flightIndex + 2]?.arrivalCity}"
                }
                else -> {
                    "${sharedViewModel.flightsCheckIn[0]?.departureTime} ${sharedViewModel.flightsCheckIn[0]?.departureCity} - " +
                            if (sharedViewModel.directFlight)
                                "${sharedViewModel.flightsCheckIn[0]?.arrivalTime} ${sharedViewModel.flightsCheckIn[0]?.arrivalCity}"
                            else
                                "${sharedViewModel.flightsCheckIn[1]?.arrivalTime} ${sharedViewModel.flightsCheckIn[1]?.arrivalCity}"
                }
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
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text =
                if(flightType == "Outbound") {
                    if (sharedViewModel.outboundDirect) "Nonstop" else "One-stop"
                }
                else if(flightType == "Inbound"){
                    if (sharedViewModel.inboundDirect) "Nonstop" else "One-stop"
                }
                else{
                    if (sharedViewModel.directFlight) "Nonstop" else "One-stop"
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

            if(flightType == "Outbound"){
                if (sharedViewModel.outboundDirect) {
                    myBookingDetailsViewModel.outboundDuration =
                        sharedViewModel.flightsMyBooking[0]?.flightDuration.toString()
                } else {
                    val (totalHours, totalMinutes) = Time.findTotalHoursMinutesMyBooking(
                        sharedViewModel.flightsMyBooking[0],
                        sharedViewModel.flightsMyBooking[1]
                    )
                    myBookingDetailsViewModel.outboundDuration = "${totalHours}h ${totalMinutes}min"
                }
            }
            else if(flightType == "Inbound"){
                if (sharedViewModel.inboundDirect) {
                    myBookingDetailsViewModel.inboundDuration =
                        sharedViewModel.flightsMyBooking[myBookingDetailsViewModel.flightIndex + 1]?.flightDuration.toString()
                } else {
                    val (totalHours, totalMinutes) = Time.findTotalHoursMinutesMyBooking(
                        sharedViewModel.flightsMyBooking[myBookingDetailsViewModel.flightIndex + 1],
                        sharedViewModel.flightsMyBooking[myBookingDetailsViewModel.flightIndex + 2]
                    )
                    myBookingDetailsViewModel.inboundDuration = "${totalHours}h ${totalMinutes}min"
                }
            }
            else{
                if (sharedViewModel.directFlight) {
                    checkInDetailsViewModel.flightDuration =
                        sharedViewModel.flightsCheckIn[0]?.flightDuration.toString()
                } else {
                    val (totalHours, totalMinutes) = Time.findTotalHoursMinutesMyBooking(
                        sharedViewModel.flightsCheckIn[0],
                        sharedViewModel.flightsCheckIn[1]
                    )
                    checkInDetailsViewModel.flightDuration = "${totalHours}h ${totalMinutes}min"
                }
            }
            Icon(
                Icons.Filled.AccessTime,
                contentDescription = "duration",
                tint = Color(0xFF0077FF),
                modifier = Modifier.padding(top = 9.5.dp, start = 25.dp)
            )
            Text(
                text =
                when (flightType) {
                    "Outbound" -> myBookingDetailsViewModel.outboundDuration
                    "Inbound" -> myBookingDetailsViewModel.inboundDuration
                    else -> checkInDetailsViewModel.flightDuration
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
                )
            )
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