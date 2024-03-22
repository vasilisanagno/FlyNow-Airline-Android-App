package com.example.flynow.ui.screens.flights.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.model.DirectFlight
import com.example.flynow.model.OneStopFlight
import com.example.flynow.model.ReservationType
import com.example.flynow.ui.screens.flights.FlightsViewModel

//component that shows the class buttons and the prices of economy, flex and business class
@Composable
fun FlightClassButtons(
    directFlight: DirectFlight?,
    oneStopFlight: OneStopFlight?,
    flightsViewModel: FlightsViewModel,
    listOfClassButtons: MutableList<ReservationType>,
    index: Int,
    type: String
) {
    if(listOfClassButtons.size!=0) {
        Column(modifier = Modifier.fillMaxSize()) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, top = 10.dp),
                color = Color(0xFF023E8A),
                thickness = 1.dp
            )
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Economy",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF00b4d8)
                )
                Text(
                    text =  "${directFlight?.economyPrice ?: (oneStopFlight!!.firstEconomyPrice + oneStopFlight.secondEconomyPrice)} €",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF00b4d8)
                )
                Button(
                    onClick = {
                        flightsViewModel.handleClassButtons(
                            directFlight = directFlight,
                            oneStopFlight = oneStopFlight,
                            flightClass = "economy",
                            type = type,
                            listOfClassButtons = listOfClassButtons,
                            index = index
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00b4d8)
                    ),
                    contentPadding = PaddingValues(start = 30.dp, end = 30.dp),
                    modifier = Modifier.height(25.dp)
                ) {
                    Text(
                        text = if(listOfClassButtons[index].economyClassClicked.value) "Selected" else "Select",
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
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, top = 10.dp),
                color = Color(0xFF023E8A),
                thickness = 1.dp
            )
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Flex",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF0096c7)
                )
                Text(
                    text = "${directFlight?.flexPrice ?: (oneStopFlight!!.firstFlexPrice + oneStopFlight.secondFlexPrice)} €",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF0096c7),
                    modifier = Modifier.padding(start = 40.dp)
                )
                Button(
                    onClick = {
                        flightsViewModel.handleClassButtons(
                            directFlight = directFlight,
                            oneStopFlight = oneStopFlight,
                            flightClass = "flex",
                            type = type,
                            listOfClassButtons = listOfClassButtons,
                            index = index
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0096c7)
                    ),
                    contentPadding = PaddingValues(start = 30.dp, end = 30.dp),
                    modifier = Modifier.height(25.dp)
                ) {
                    Text(
                        text = if(listOfClassButtons[index].flexClassClicked.value) "Selected" else "Select",
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
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, top = 10.dp),
                color = Color(0xFF023E8A),
                thickness = 1.dp
            )
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Business",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF0077b6)
                )
                Text(
                    text = "${directFlight?.businessPrice ?: (oneStopFlight!!.firstBusinessPrice + oneStopFlight.secondBusinessPrice)} €",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF0077b6),
                    modifier = Modifier.padding(start = 5.dp)
                )
                Button(
                    onClick = {
                        flightsViewModel.handleClassButtons(
                            directFlight = directFlight,
                            oneStopFlight = oneStopFlight,
                            flightClass = "business",
                            type = type,
                            listOfClassButtons = listOfClassButtons,
                            index = index
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0077b6)
                    ),
                    contentPadding = PaddingValues(start = 30.dp, end = 30.dp),
                    modifier = Modifier.height(25.dp)
                ) {
                    Text(
                        text = if(listOfClassButtons[index].businessClassClicked.value) "Selected" else "Select",
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
    }
}