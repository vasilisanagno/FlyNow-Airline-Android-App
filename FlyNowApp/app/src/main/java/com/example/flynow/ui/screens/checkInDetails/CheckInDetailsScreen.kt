package com.example.flynow.ui.screens.checkInDetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.CheckIn
import com.example.flynow.navigation.CheckInDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowButton
import com.example.flynow.ui.screens.checkInDetails.components.PassengerInfoCheckBox
import com.example.flynow.ui.screens.checkInDetails.components.ShowDialogCheckInFlightsInfo
import com.example.flynow.ui.screens.checkInDetails.components.ShowDialogToCommitCheckIn
import com.example.flynow.ui.screens.myBookingDetails.MyBookingDetailsViewModel
import com.example.flynow.ui.screens.myBookingDetails.components.BaggageInfo
import com.example.flynow.ui.screens.myBookingDetails.components.BookingReferenceInfo
import com.example.flynow.ui.screens.myBookingDetails.components.FlightInfo
import com.example.flynow.ui.screens.myBookingDetails.components.PetsInfo
import com.example.flynow.ui.screens.myBookingDetails.components.SeatsInfo
import com.example.flynow.ui.screens.myBookingDetails.components.ShowDialogPassengerInfo
import com.example.flynow.ui.screens.myBookingDetails.components.WifiOnBoardInfo
import com.example.flynow.utils.Constants

//screen that shows the check-in details
//navController to navigate backward or forward to other pages
//shared view model is for the shared data and book view model for the data that the state is kept in this screen
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CheckInDetailsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    myBookingDetailsViewModel: MyBookingDetailsViewModel,
    checkInDetailsViewModel: CheckInDetailsViewModel
) {
    Column(modifier = Modifier
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //"Back" button
            IconButton(onClick = {
                myBookingDetailsViewModel.initializeVariables()
                checkInDetailsViewModel.initializeVariables()
                navController.navigate(CheckIn.route) {
                    popUpTo(CheckInDetails.route)
                    launchSingleTop = true
                }
            }) {
                Icon(
                    Icons.Outlined.ArrowBackIos,
                    contentDescription = "back",
                    tint = Color(0xFF023E8A)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Check-In",
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
                    Icons.Filled.FactCheck,
                    contentDescription = "checkin",
                    tint = Color(0xFF023E8A),
                    modifier = Modifier.padding(start = 5.dp, end = 45.dp)
                )
            }
        }
    Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))

    if (!sharedViewModel.backButton) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Constants.gradient)
                .verticalScroll(rememberScrollState())
        ) {
            //Component that shows image of "Check-in" screen and the booking reference
            BookingReferenceInfo(sharedViewModel = sharedViewModel, imageId = R.drawable.checkin)

            //Component that shows flight information
            FlightInfo(
                sharedViewModel = sharedViewModel,
                myBookingDetailsViewModel = myBookingDetailsViewModel,
                flightType = "Flight",
                icon = Icons.Filled.AirplanemodeActive,
                checkInDetailsViewModel = checkInDetailsViewModel
            )

            //Component that shows passengers' info and checkbox for check in selection
            PassengerInfoCheckBox(
                sharedViewModel = sharedViewModel,
                myBookingDetailsViewModel = myBookingDetailsViewModel
            )

            //Component that shows passengers' seats
            SeatsInfo(
                sharedViewModel = sharedViewModel,
                flightType = "Checkin"
            )

            //Component that shows passengers' baggage
            BaggageInfo(
                sharedViewModel = sharedViewModel,
                flightType = "Checkin"
            )

            //Component that shows the selection of wifi on board
            WifiOnBoardInfo(wifiType = sharedViewModel.wifiOnBoardCheckIn)

            //Component that shows the selection of petsize
            PetsInfo(petSize = sharedViewModel.petSizeCheckIn)

            Text(
                text = "Select all passengers to continue with check-in",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 10.dp, top = 20.dp, end = 10.dp),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                color = Color(0xFF023E8A)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                FlyNowButton(
                    text = "Check-in",
                    onClick = {checkInDetailsViewModel.showDialogCheckIn = true},
                    modifier = Modifier.padding(top = 10.dp, bottom = 30.dp),
                    enabled = sharedViewModel.checkedState.all { it.value },
                )

                }
            }
            if(myBookingDetailsViewModel.showDialogPassenger) {
                ShowDialogPassengerInfo(
                    sharedViewModel = sharedViewModel,
                    myBookingDetailsViewModel = myBookingDetailsViewModel
                )
            }
            if(checkInDetailsViewModel.showDialogFlights) {
                ShowDialogCheckInFlightsInfo(
                    sharedViewModel = sharedViewModel,
                    checkInDetailsViewModel = checkInDetailsViewModel
                )
            }
            ShowDialogToCommitCheckIn(
                navController = navController,
                checkInDetailsViewModel = checkInDetailsViewModel
            )
        }
    }
}