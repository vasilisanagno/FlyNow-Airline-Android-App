package com.example.flynow.ui.screens.checkInDetails.components

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
import com.example.flynow.ui.screens.checkInDetails.CheckInDetailsViewModel

//alert dialog for more details in the flights
//and takes as parameters the info that must show in the dialog
//like duration and flightsMyBooking list that has all the information about flights
@Composable
fun ShowDialogCheckInFlightsInfo(
    sharedViewModel: SharedViewModel,
    checkInDetailsViewModel: CheckInDetailsViewModel
){
    val repeatTimes = remember {
        mutableIntStateOf(0)
    }
    if(sharedViewModel.directFlight){
        repeatTimes.intValue = 1
    }
    else{
        repeatTimes.intValue = 2
    }

    Box(
        contentAlignment = Alignment.Center
    ){
        AlertDialog(
            onDismissRequest = { checkInDetailsViewModel.showDialogFlights = false },
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
                                if (repeatTimes.intValue == 1) "Flight" else "Flights",
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
                                    text = "${checkInDetailsViewModel.flightDuration}, " + if (sharedViewModel.directFlight) "nonstop" else "one-stop",
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
                        if(!sharedViewModel.directFlight) {
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
                                text = "${sharedViewModel.flightsCheckIn[index]?.flightDate}",
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
                            text = "${sharedViewModel.flightsCheckIn[index]?.departureTime}     ${sharedViewModel.flightsCheckIn[index]?.departureCity} (${sharedViewModel.flightsCheckIn[index]?.departureAirp})",
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
                                    text = "${sharedViewModel.flightsCheckIn[index]?.flightDuration}",
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
                            text = "${sharedViewModel.flightsCheckIn[index]?.arrivalTime}     ${sharedViewModel.flightsCheckIn[index]?.arrivalCity} (${sharedViewModel.flightsCheckIn[index]?.arrivalAirp})",
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
                                text = "${sharedViewModel.flightsCheckIn[index]?.flightId}",
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
                                text = "${sharedViewModel.flightsCheckIn[index]?.airplaneModel}",
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
                                text = "${sharedViewModel.flightsCheckIn[index]?.classType}",
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
                        checkInDetailsViewModel.showDialogFlights = false
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