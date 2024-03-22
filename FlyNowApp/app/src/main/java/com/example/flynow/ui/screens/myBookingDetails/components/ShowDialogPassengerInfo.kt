package com.example.flynow.ui.screens.myBookingDetails.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.myBookingDetails.MyBookingDetailsViewModel

//alert dialog for more details in passengers that shows the information that it is in
//the list of passengerMyBooking
@Composable
fun ShowDialogPassengerInfo(
    sharedViewModel: SharedViewModel,
    myBookingDetailsViewModel: MyBookingDetailsViewModel
){
    Box(
        contentAlignment = Alignment.Center
    ){
        AlertDialog(
            modifier = Modifier
                .width(400.dp)
                .height(300.dp),
            onDismissRequest = { myBookingDetailsViewModel.showDialogPassenger = false },
            title = {
                Text(
                    text = "Passenger's Information",
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
                Column(modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())) {

                    Row {
                        Text(
                            "Email: ",
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
                                if(sharedViewModel.passengersMyBooking.isEmpty())
                                    sharedViewModel.passengersCheckIn[myBookingDetailsViewModel.selectedIndexDetails]?.email!!.value
                                else
                                    sharedViewModel.passengersMyBooking[myBookingDetailsViewModel.selectedIndexDetails]?.email!!.value,
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
                            "Phone Number: ",
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
                                if(sharedViewModel.passengersMyBooking.isEmpty())
                                    sharedViewModel.passengersCheckIn[myBookingDetailsViewModel.selectedIndexDetails]?.phonenumber!!.value
                                else
                                    sharedViewModel.passengersMyBooking[myBookingDetailsViewModel.selectedIndexDetails]?.phonenumber!!.value,

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
                            "Birthdate: ",
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
                                if(sharedViewModel.passengersMyBooking.isEmpty())
                                    sharedViewModel.passengersCheckIn[myBookingDetailsViewModel.selectedIndexDetails]?.birthdate!!.value
                                else
                                    sharedViewModel.passengersMyBooking[myBookingDetailsViewModel.selectedIndexDetails]?.birthdate!!.value,
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
                            "Gender: ",
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
                                if(sharedViewModel.passengersMyBooking.isEmpty())
                                    sharedViewModel.passengersCheckIn[myBookingDetailsViewModel.selectedIndexDetails]?.gender!!.value
                                else
                                    sharedViewModel.passengersMyBooking[myBookingDetailsViewModel.selectedIndexDetails]?.gender!!.value,
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        myBookingDetailsViewModel.showDialogPassenger = false
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
            properties = DialogProperties(dismissOnClickOutside = true)
        )
    }
}