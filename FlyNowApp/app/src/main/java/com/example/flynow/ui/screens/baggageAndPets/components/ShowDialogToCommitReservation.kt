package com.example.flynow.ui.screens.baggageAndPets.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.BaggageAndPets
import com.example.flynow.navigation.Home
import com.example.flynow.navigation.Seats
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel
import com.example.flynow.ui.screens.book.BookViewModel
import com.example.flynow.ui.screens.flights.FlightsViewModel
import com.example.flynow.ui.screens.passengers.PassengersViewModel
import com.example.flynow.ui.screens.seats.SeatsViewModel
import kotlinx.coroutines.delay

//component that shows the alert dialog that it is for completion of the reservation
//and call the api to insert the new booking in database
@Composable
fun ShowDialogToCommitReservation(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    baggageAndPetsViewModel: BaggageAndPetsViewModel,
    seatsViewModel: SeatsViewModel,
    passengersViewModel: PassengersViewModel,
    flightsViewModel: FlightsViewModel,
    bookViewModel: BookViewModel
) {

    val showDialogConfirm = remember {
        mutableStateOf(false)
    }
    val insertNewBooking = remember {
        mutableStateOf(false)
    }
    var bookingReference by remember {
        mutableStateOf("")
    }
    val showBookingReference = remember {
        mutableStateOf(false)
    }

    //api that makes insert query in the database to add the new data for the new booking that is created from the user
    LaunchedEffect(insertNewBooking.value) {
        if (insertNewBooking.value) {
            baggageAndPetsViewModel.insertNewBooking()
            delay(5000)
            if(baggageAndPetsViewModel.bookingReference == "") {
                showBookingReference.value = false
                showDialogConfirm.value = false
                sharedViewModel.seats.clear()
                baggageAndPetsViewModel.goToPreviousScreen()
                sharedViewModel.finishReservation = 1
                seatsViewModel.goToPreviousScreen()
                sharedViewModel.bookingFailed = true
                passengersViewModel.prepareForTheNextScreen()
                navController.navigate(Seats.route) {
                    popUpTo(BaggageAndPets.route)
                    launchSingleTop = true
                }
            }
            else {
                bookingReference = baggageAndPetsViewModel.bookingReference
            }
            insertNewBooking.value = false
            showBookingReference.value = true
        }
    }

    if (baggageAndPetsViewModel.showDialog || showDialogConfirm.value) {
        Box(contentAlignment = Alignment.Center) {
            AlertDialog(
                onDismissRequest = { baggageAndPetsViewModel.showDialog = false },
                title = {
                    Text(
                        text = if (baggageAndPetsViewModel.showDialog) "Confirm Reservation"
                        else if (!insertNewBooking.value) "Reservation Booked Successfully!"
                        else "",
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
                    if (!insertNewBooking.value) {
                        Icon(
                            if (baggageAndPetsViewModel.showDialog) Icons.Filled.QuestionMark
                            else Icons.Filled.Verified,
                            contentDescription = "question",
                            modifier =
                            if (baggageAndPetsViewModel.showDialog) Modifier.padding(start = 190.dp)
                            else Modifier.padding(top = 33.dp, start = 125.dp),
                            tint = Color(0xFF023E8A)
                        )
                    }
                },
                text = {
                    if (baggageAndPetsViewModel.showDialog && !insertNewBooking.value) {
                        Text(
                            text = "Are you sure you want to book this flight?",
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
                    if (insertNewBooking.value) {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp, bottom = 15.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF023E8A)
                            )
                        }
                    }
                    if (showBookingReference.value) {
                        Text(
                            text = "Your booking reference is: $bookingReference",
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
                confirmButton = {
                    if (baggageAndPetsViewModel.showDialog && !insertNewBooking.value) {
                        Button(
                            onClick = {
                                insertNewBooking.value = true
                                baggageAndPetsViewModel.showDialog = false
                                showDialogConfirm.value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF023E8A)
                            )
                        ) {
                            Text(
                                "Yes",
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
                    } else {
                        if (!insertNewBooking.value) {
                            Button(
                                onClick = {
                                    showBookingReference.value = false
                                    showDialogConfirm.value = false
                                    baggageAndPetsViewModel.initializationVariables()
                                    seatsViewModel.goToPreviousScreen()
                                    passengersViewModel.goToPreviousScreen()
                                    flightsViewModel.goToPreviousScreen()
                                    bookViewModel.initializeVariables()
                                    navController.navigate(Home.route) {
                                        popUpTo(BaggageAndPets.route)
                                        launchSingleTop = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF023E8A)
                                )
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
                        }
                    }
                },
                dismissButton = {
                    if (baggageAndPetsViewModel.showDialog && !insertNewBooking.value) {
                        Button(
                            onClick = {
                                baggageAndPetsViewModel.showDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF023E8A)
                            )
                        ) {
                            Text(
                                "No",
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
                    }
                },
                containerColor = Color(0xFFEBF2FA),
                textContentColor = Color(0xFF023E8A),
                titleContentColor = Color(0xFF023E8A),
                tonalElevation = 30.dp,
                properties = DialogProperties(dismissOnClickOutside = false)
            )
        }
    }
}