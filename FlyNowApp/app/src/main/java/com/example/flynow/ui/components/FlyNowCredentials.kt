package com.example.flynow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.outlined.AirlineSeatReclineNormal
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.BaggageFromMore
import com.example.flynow.navigation.CheckIn
import com.example.flynow.navigation.More
import com.example.flynow.navigation.PetsFromMore
import com.example.flynow.navigation.UpgradeClass
import com.example.flynow.navigation.Wifi
import com.example.flynow.ui.SharedViewModel
import kotlinx.coroutines.delay

//component that shows the credentials text fields in many screens
//and check if the booking exists after the "continue" button is clicked
@Composable
fun FlyNowCredentials(
    state: String,
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val checkBooking = remember {
        mutableStateOf(false)
    }

    //api for checking the booking if exists and continue to other routes
    LaunchedEffect(checkBooking.value) {
        if (checkBooking.value && sharedViewModel.textLastname != "" && sharedViewModel.textBookingId != "") {
            sharedViewModel.checkBookingExists()
            delay(1000)
            if(sharedViewModel.bookingExists) {
                when (state) {
                    "Wifi" -> {
                        sharedViewModel.wifiMore = true
                    }
                    "UpgradeClass" -> {
                        sharedViewModel.upgradeToBusinessMore = true
                    }
                    "BaggageFromMore" -> {
                        sharedViewModel.baggageFromMore = true
                    }
                    "PetsFromMore" -> {
                        sharedViewModel.petsFromMore = true
                    }
                    "MyBooking" -> {
                        sharedViewModel.myBooking = true
                    }
                    "Check-In" -> {
                        sharedViewModel.checkIn = true
                    }
                }
            }
            else {
                sharedViewModel.hasError = true
                checkBooking.value = false
            }
        }
        else {
            checkBooking.value = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(state != "MyBooking") {
                IconButton(
                    onClick = {
                        sharedViewModel.textLastname = ""
                        sharedViewModel.textBookingId = ""
                        sharedViewModel.buttonClickedCredentials = false
                        sharedViewModel.hasError = false
                        sharedViewModel.checkInOpen = true
                        when (state) {
                            "UpgradeClass" -> {
                                navController.navigate(More.route) {
                                    popUpTo(UpgradeClass.route)
                                    launchSingleTop = true
                                }
                            }
                            "PetsFromMore" -> {
                                navController.navigate(More.route) {
                                    popUpTo(PetsFromMore.route)
                                    launchSingleTop = true
                                }
                            }
                            "BaggageFromMore" -> {
                                navController.navigate(More.route) {
                                    popUpTo(BaggageFromMore.route)
                                    launchSingleTop = true
                                }
                            }
                            "Wifi" -> {
                                navController.navigate(More.route) {
                                    popUpTo(Wifi.route)
                                    launchSingleTop = true
                                }
                            }
                            "Check-In" -> {
                                navController.navigate(More.route) {
                                    popUpTo(CheckIn.route)
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Outlined.ArrowBackIos,
                        contentDescription = "back",
                        tint = Color(0xFF023E8A)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text =
                    when (state) {
                        "PetsFromMore" -> "Pets"
                        "BaggageFromMore" -> "Baggage"
                        "Wifi" -> "Wifi οn Board"
                        "UpgradeClass" -> "Upgrade to Business Class"
                        "MyBooking" -> "Find your Booking"
                        "Check-In" -> "Check-In"
                        else -> "Select Seat"
                    },
                    modifier = if(state == "MyBooking") Modifier.padding(start = 30.dp, top = 5.dp, bottom = 10.dp) else Modifier,
                    fontSize = 22.sp,
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
                Icon(
                    when (state) {
                        "PetsFromMore" -> Icons.Outlined.Pets
                        "BaggageFromMore" -> Icons.Outlined.Luggage
                        "Wifi" -> Icons.Outlined.Wifi
                        "UpgradeClass" -> Icons.Outlined.KeyboardDoubleArrowUp
                        "MyBooking" -> Icons.Filled.AirplaneTicket
                        "Check-In" -> Icons.Filled.FactCheck
                        else -> Icons.Outlined.AirlineSeatReclineNormal
                    },
                    contentDescription =
                    when (state) {
                        "PetsFromMore" -> "Pets"
                        "BaggageFromMore" -> "Baggage"
                        "Wifi" -> "Wifi"
                        "UpgradeClass" -> "UpgradeClass"
                        "MyBooking" -> "MyBooking"
                        "Check-In" -> "Check-In"
                        else -> "SeatSelection"
                    },
                    modifier = Modifier.padding(start = 5.dp, top = if(state != "MyBooking") 2.dp else 10.dp, end = 40.dp),
                    tint = Color(0xFF023E8A)
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
        CredentialsInfo(
            state = state,
            sharedViewModel = sharedViewModel,
            checkBooking = checkBooking
        )
    }
}