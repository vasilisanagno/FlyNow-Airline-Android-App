package com.example.flynow.ui.screens.myBookingDetails

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
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.Home
import com.example.flynow.navigation.MyBooking
import com.example.flynow.navigation.MyBookingDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowButton
import com.example.flynow.ui.screens.checkInDetails.CheckInDetailsViewModel
import com.example.flynow.ui.screens.myBookingDetails.components.BaggageInfo
import com.example.flynow.ui.screens.myBookingDetails.components.BookingReferenceInfo
import com.example.flynow.ui.screens.myBookingDetails.components.CarInfo
import com.example.flynow.ui.screens.myBookingDetails.components.DeleteBookingButton
import com.example.flynow.ui.screens.myBookingDetails.components.FlightInfo
import com.example.flynow.ui.screens.myBookingDetails.components.PassengersInfo
import com.example.flynow.ui.screens.myBookingDetails.components.PetsInfo
import com.example.flynow.ui.screens.myBookingDetails.components.PriceInfo
import com.example.flynow.ui.screens.myBookingDetails.components.SeatsInfo
import com.example.flynow.ui.screens.myBookingDetails.components.ShowDialogDeleteBooking
import com.example.flynow.ui.screens.myBookingDetails.components.ShowDialogFlightsInfo
import com.example.flynow.ui.screens.myBookingDetails.components.ShowDialogPassengerInfo
import com.example.flynow.ui.screens.myBookingDetails.components.WifiOnBoardInfo
import com.example.flynow.utils.Constants
import kotlinx.coroutines.delay

//In this screen the user of the app can see the details of his booking
//navController helps to navigate to previous page or next page,
//shared view model is for the shared data and book view model for the data that the state is kept in this screen
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyBookingDetailsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    myBookingDetailsViewModel: MyBookingDetailsViewModel,
    checkInDetailsViewModel: CheckInDetailsViewModel)
{
    //api that deletes the booking and all the references about this booking
    LaunchedEffect(myBookingDetailsViewModel.deleteBooking) {
        if (myBookingDetailsViewModel.deleteBooking) {
            myBookingDetailsViewModel.deleteBookingDetails()
            delay(3000)
            myBookingDetailsViewModel.deleteBooking = false
        }
    }

    //alert dialog to complete the deletion of the booking
    if (myBookingDetailsViewModel.showDialog || myBookingDetailsViewModel.showDialogConfirm) {
        ShowDialogDeleteBooking(
            navController = navController,
            myBookingDetailsViewModel = myBookingDetailsViewModel
        )
    }

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
            //"Back" button to find your booking page
            IconButton(onClick = {
                myBookingDetailsViewModel.initializeVariables()
                navController.navigate(MyBooking.route) {
                    popUpTo(MyBookingDetails.route)
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
                    text = "My Booking",
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
                    Icons.Filled.AirplaneTicket,
                    contentDescription = "bookingId",
                    tint = Color(0xFF023E8A),
                    modifier = Modifier.padding(start = 5.dp, end = 45.dp, top = 5.dp)
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))

        if(!sharedViewModel.backButton) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Constants.gradient)
                    .verticalScroll(rememberScrollState())
            ) {
                //Component that shows image of "MyBooking" screen and the booking reference
                BookingReferenceInfo(sharedViewModel = sharedViewModel, imageId = R.drawable.mybook)

                //Component that shows flight information for the outbound
                FlightInfo(
                    sharedViewModel = sharedViewModel,
                    myBookingDetailsViewModel = myBookingDetailsViewModel,
                    flightType = "Outbound",
                    icon = Icons.Filled.FlightTakeoff,
                    checkInDetailsViewModel = checkInDetailsViewModel
                )

                //Component that shows flight information for the inbound (if exists)
                if(!sharedViewModel.oneWay){
                    FlightInfo(
                        sharedViewModel = sharedViewModel,
                        myBookingDetailsViewModel = myBookingDetailsViewModel,
                        flightType = "Inbound",
                        icon = Icons.Filled.FlightLand,
                        checkInDetailsViewModel = checkInDetailsViewModel
                    )
                }

                //Component that shows passengers' information
                PassengersInfo(
                    sharedViewModel = sharedViewModel,
                    myBookingDetailsViewModel = myBookingDetailsViewModel
                )

                //Component that shows seats information and check-in for the outbound
                SeatsInfo(
                    sharedViewModel = sharedViewModel,
                    flightType = "Outbound"
                )

                if(!sharedViewModel.oneWay) {
                    //Component that shows seats information and check-in for the inbound (if exists)
                    SeatsInfo(
                        sharedViewModel = sharedViewModel,
                        flightType = "Inbound"
                    )
                }

                //Component that shows baggage information for the outbound
                BaggageInfo(
                    sharedViewModel = sharedViewModel,
                    flightType = "Outbound"
                )

                if(!sharedViewModel.oneWay) {
                    //Component that shows baggage information for the inbound (if exists)
                    BaggageInfo(
                        sharedViewModel = sharedViewModel,
                        flightType = "Inbound"
                    )
                }

                //Component that shows wifi information for the booking
                WifiOnBoardInfo(wifiType = sharedViewModel.wifiOnBoard)

                //Component that shows pet information for the booking
                PetsInfo(petSize = sharedViewModel.petSizeMyBooking)

                //Component that shows car information for the booking
                CarInfo(sharedViewModel = sharedViewModel)

                //Component that shows the price of the flights' booking
                PriceInfo(sharedViewModel = sharedViewModel, priceType = "Booking")
                //Component that shows the price of the renting cars booking
                PriceInfo(sharedViewModel = sharedViewModel, priceType = "Renting Cars")
                //Component that shows the total price
                PriceInfo(sharedViewModel = sharedViewModel, priceType = "Total")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    FlyNowButton(
                        text = "Home",
                        onClick = {
                            myBookingDetailsViewModel.initializeVariables()
                            sharedViewModel.selectedIndex = 0
                            navController.navigate(Home.route) {
                                popUpTo(MyBookingDetails.route)
                                launchSingleTop = true
                            }
                        }
                    )
                    
                    DeleteBookingButton(myBookingDetailsViewModel = myBookingDetailsViewModel)
                    
                }
            }
            
            if (myBookingDetailsViewModel.showDialogPassenger) {
                ShowDialogPassengerInfo(
                    sharedViewModel = sharedViewModel,
                    myBookingDetailsViewModel = myBookingDetailsViewModel
                )
            }
            if (myBookingDetailsViewModel.showDialogFlights) {
                if (myBookingDetailsViewModel.outboundFlight) {
                    ShowDialogFlightsInfo(
                        myBookingDetailsViewModel = myBookingDetailsViewModel,
                        sharedViewModel = sharedViewModel,
                        duration = myBookingDetailsViewModel.outboundDuration
                    )
                } else {
                    ShowDialogFlightsInfo(
                        myBookingDetailsViewModel = myBookingDetailsViewModel,
                        sharedViewModel = sharedViewModel,
                        duration = myBookingDetailsViewModel.inboundDuration
                    )
                }
            }
        }
    }
}

