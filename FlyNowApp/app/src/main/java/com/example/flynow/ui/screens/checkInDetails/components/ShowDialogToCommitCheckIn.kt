package com.example.flynow.ui.screens.checkInDetails.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.flynow.navigation.CheckInDetails
import com.example.flynow.navigation.Home
import com.example.flynow.ui.screens.checkInDetails.CheckInDetailsViewModel
import kotlinx.coroutines.delay

//alert dialog for committing the check-in process
@Composable
fun ShowDialogToCommitCheckIn(
    navController: NavController,
    checkInDetailsViewModel: CheckInDetailsViewModel
) {
    //api that updates the check-in for the passengers in specific flight/s
    LaunchedEffect(checkInDetailsViewModel.updateCheckIn) {
        if (checkInDetailsViewModel.updateCheckIn) {
            checkInDetailsViewModel.updateCheckIn()
            delay(3000)
            checkInDetailsViewModel.updateCheckIn = false
        }
    }

    //alert dialog to complete the update of the check in
    if (checkInDetailsViewModel.showDialogCheckIn || checkInDetailsViewModel.showDialogConfirmed) {
        Box(contentAlignment = Alignment.Center) {
            AlertDialog(
                onDismissRequest = { checkInDetailsViewModel.showDialogCheckIn = false },
                title = {
                    Text(
                        text =
                        if (checkInDetailsViewModel.showDialogCheckIn)
                            "Confirm Check-In"
                        else if(!checkInDetailsViewModel.updateCheckIn)
                            "The Check-In Was Done Successfully!"
                        else
                            "",
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
                    if(!checkInDetailsViewModel.updateCheckIn) {
                        Icon(
                            if (checkInDetailsViewModel.showDialogCheckIn) Icons.Filled.QuestionMark
                            else Icons.Filled.Verified,
                            contentDescription = "question",
                            modifier =
                            if (checkInDetailsViewModel.showDialogCheckIn) Modifier.padding(start = 160.dp, top = 0.dp)
                            else Modifier.padding(top = 33.dp, start = 125.dp),
                            tint = Color(0xFF023E8A)
                        )
                    }
                },
                text = {
                    if (checkInDetailsViewModel.showDialogCheckIn && !checkInDetailsViewModel.updateCheckIn) {
                        Text(
                            text = "Are you sure you want to check-in?",
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
                    if (checkInDetailsViewModel.updateCheckIn) {
                        Column(modifier = Modifier
                            .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF023E8A)
                            )
                        }
                    }
                },
                confirmButton = {
                    if (checkInDetailsViewModel.showDialogCheckIn && !checkInDetailsViewModel.updateCheckIn) {
                        Button(
                            onClick = {
                                checkInDetailsViewModel.updateCheckIn = true
                                checkInDetailsViewModel.showDialogCheckIn = false
                                checkInDetailsViewModel.showDialogConfirmed = true
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
                        if(!checkInDetailsViewModel.updateCheckIn) {
                            Button(
                                onClick = {
                                    checkInDetailsViewModel.showDialogCheckIn = false
                                    checkInDetailsViewModel.showDialogConfirmed = false
                                    checkInDetailsViewModel.initializeVariables()
                                    navController.navigate(Home.route) {
                                        popUpTo(CheckInDetails.route)
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
                    if (checkInDetailsViewModel.showDialogCheckIn && !checkInDetailsViewModel.updateCheckIn) {
                        Button(
                            onClick = {
                                checkInDetailsViewModel.showDialogCheckIn = false
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