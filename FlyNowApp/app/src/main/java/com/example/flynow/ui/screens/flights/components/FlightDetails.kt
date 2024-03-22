package com.example.flynow.ui.screens.flights.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.window.DialogProperties
import com.example.flynow.R
import com.example.flynow.model.DirectFlight
import com.example.flynow.model.OneStopFlight

//component that shows the alert dialog for the flight details with more info about the flight
@Composable
fun FlightDetails(
    directFlight: DirectFlight?,
    oneStopFlight: OneStopFlight?,
    returnOrNot: Boolean,
    totalHours: Int,
    totalMinutes: Int,
    showDialog: MutableState<Boolean>
) {
    if(showDialog.value) {
        Box(contentAlignment = Alignment.Center) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text("Flight Details")
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = rememberScrollState())
                    ) {
                        Text(
                            if (returnOrNot) "Inbound" else "Outbound",
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
                                text = if (directFlight != null) "${directFlight.flightDuration}, nonstop"
                                else "${
                                    if (totalHours != 0 && totalMinutes != 0) "$totalHours h $totalMinutes min"
                                    else if (totalHours != 0) "$totalHours h"
                                    else "$totalMinutes min"
                                }, 1 stop",
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
                        for (i in 0 until if (directFlight != null) 1 else 2) {
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                thickness = 1.dp,
                                color = Color(0xFF023E8A)
                            )
                            if (oneStopFlight != null) {
                                Text(
                                    if (i == 0) "1st Flight" else "2nd Flight",
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 10.dp)
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
                                    text = directFlight?.flightDate
                                        ?: if (i == 0) oneStopFlight!!.firstFlightDate else oneStopFlight!!.secondFlightDate,
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
                            DepartureArrivalInfo(
                                directFlight = directFlight,
                                oneStopFlight = oneStopFlight,
                                i = i
                            )
                            Row {
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
                                    text = directFlight?.flightId
                                        ?: if (i == 0) oneStopFlight!!.firstFlightId else oneStopFlight!!.secondFlightId,
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
                                    text = directFlight?.airplaneModel
                                        ?: if (i == 0) oneStopFlight!!.firstAirplaneModel else oneStopFlight!!.secondAirplaneModel,
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
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
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
                modifier = Modifier.height(400.dp).width(400.dp),
                properties = DialogProperties(dismissOnClickOutside = true)
            )
        }
    }
}