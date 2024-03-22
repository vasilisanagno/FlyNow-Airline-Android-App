package com.example.flynow.ui.screens.cars.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.flynow.navigation.Cars
import com.example.flynow.navigation.Home
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.cars.CarsViewModel

//component that shows the alert dialog for the completion
//of the renting and go back to the home page
@Composable
fun ShowCarDialog(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    carsViewModel: CarsViewModel
) {
    if (carsViewModel.showDialog || carsViewModel.showDialogConfirm) {
        Box(contentAlignment = Alignment.Center) {
            AlertDialog(
                onDismissRequest = { carsViewModel.showDialog = false },
                title = {
                    Text(
                        text =
                        if (carsViewModel.showDialog)
                            "Confirm The Rental Of This Car"
                        else if(!carsViewModel.insertNewBookingOfCar)
                            "The Car Rental Added To Your Reservation Successfully!"
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
                    if(!carsViewModel.insertNewBookingOfCar) {
                        Icon(
                            if (carsViewModel.showDialog) Icons.Filled.QuestionMark
                            else Icons.Filled.Verified,
                            contentDescription = "question",
                            modifier =
                            if (carsViewModel.showDialog) Modifier.padding(start = 27.dp, top = 31.dp)
                            else Modifier.padding(top = 66.dp, start = 125.dp),
                            tint = Color(0xFF023E8A)
                        )
                    }
                },
                text = {
                    if (carsViewModel.showDialog && !carsViewModel.insertNewBookingOfCar) {
                        Text(
                            text = "Are you sure you want to rent this car?",
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
                    if (carsViewModel.insertNewBookingOfCar) {
                        Column(modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF023E8A)
                            )
                        }
                    }
                },
                confirmButton = {
                    if (carsViewModel.showDialog && !carsViewModel.insertNewBookingOfCar) {
                        Button(
                            onClick = {
                                carsViewModel.insertNewBookingOfCar = true
                                carsViewModel.showDialog = false
                                carsViewModel.showDialogConfirm = true
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
                        if(!carsViewModel.insertNewBookingOfCar) {
                            Button(
                                onClick = {
                                    sharedViewModel.selectedIndex = 0
                                    carsViewModel.initializeVariables()
                                    navController.navigate(Home.route) {
                                        popUpTo(Cars.route)
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
                    if (carsViewModel.showDialog && !carsViewModel.insertNewBookingOfCar) {
                        Button(
                            onClick = {
                                carsViewModel.showDialog = false
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