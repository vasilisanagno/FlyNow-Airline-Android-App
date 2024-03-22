package com.example.flynow.ui.screens.myBookingDetails.components

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
import com.example.flynow.navigation.Home
import com.example.flynow.navigation.MyBookingDetails
import com.example.flynow.ui.screens.myBookingDetails.MyBookingDetailsViewModel

//Component that shows the delete booking dialog
// so the user to confirm the deleting of the booking
@Composable
fun ShowDialogDeleteBooking(
    navController: NavController,
    myBookingDetailsViewModel: MyBookingDetailsViewModel
){
    Box(contentAlignment = Alignment.Center) {
        AlertDialog(
            onDismissRequest = { myBookingDetailsViewModel.showDialog = false },
            title = {
                Text(
                    text = if (myBookingDetailsViewModel.showDialog) "Confirm Deleting This Booking" else if(!myBookingDetailsViewModel.deleteBooking) "The Cancellation Of The Reservation Was Done Successfully!" else "",
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
                if(!myBookingDetailsViewModel.deleteBooking) {
                    Icon(
                        if (myBookingDetailsViewModel.showDialog) Icons.Filled.QuestionMark
                        else Icons.Filled.Verified,
                        contentDescription = "question",
                        modifier =
                        if (myBookingDetailsViewModel.showDialog) Modifier.padding(start = 72.dp, top = 33.dp)
                        else Modifier.padding(top = 66.dp, start = 125.dp),
                        tint = Color(0xFF023E8A)
                    )
                }
            },
            text = {
                if (myBookingDetailsViewModel.showDialog && !myBookingDetailsViewModel.deleteBooking) {
                    Text(
                        text = "Are you sure you want to delete this booking?",
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
                if (myBookingDetailsViewModel.deleteBooking) {
                    Column(modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Color(0xFF023E8A)
                        )
                    }
                }
            },
            confirmButton = {
                if (myBookingDetailsViewModel.showDialog && !myBookingDetailsViewModel.deleteBooking) {
                    Button(
                        onClick = {
                            myBookingDetailsViewModel.deleteBooking = true
                            myBookingDetailsViewModel.showDialog = false
                            myBookingDetailsViewModel.showDialogConfirm = true
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
                    if(!myBookingDetailsViewModel.deleteBooking) {
                        Button(
                            onClick = {
                                myBookingDetailsViewModel.initializeVariables()
                                navController.navigate(Home.route) {
                                    popUpTo(MyBookingDetails.route)
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
                if (myBookingDetailsViewModel.showDialog && !myBookingDetailsViewModel.deleteBooking) {
                    Button(
                        onClick = {
                            myBookingDetailsViewModel.showDialog = false
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