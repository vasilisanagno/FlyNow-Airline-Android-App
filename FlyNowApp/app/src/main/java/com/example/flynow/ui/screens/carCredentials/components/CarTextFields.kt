package com.example.flynow.ui.screens.carCredentials.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AirplaneTicket
import androidx.compose.material.icons.outlined.LocalAirport
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.Airports
import com.example.flynow.navigation.CarCredentials
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowTextField
import com.example.flynow.ui.screens.carCredentials.CarCredentialsViewModel

//component that shows the text fields in the car credentials screen that the user
//must fill out to search cars to book
@Composable
fun CarTextFields(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    carCredentialsViewModel: CarCredentialsViewModel
) {
    //text field for the airport location for the renting of car
    FlyNowTextField(
        text = sharedViewModel.locationToRentCar,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
            .clickable(enabled = false, onClickLabel = null, onClick = {}),
        label = "Location",
        readOnly = true,
        onTextChange = {
            sharedViewModel.locationToRentCar = it
            sharedViewModel.buttonClickedCredentials = false
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        leadingIcon = {
            IconButton(onClick = {
                //goes to the airports screen to select an airport
                sharedViewModel.rentCar = true
                sharedViewModel.whatAirport = 0
                navController.navigate(Airports.route) {
                    popUpTo(CarCredentials.route)
                    launchSingleTop = true
                }
            }) {
                Icon(
                    Icons.Outlined.LocalAirport,
                    contentDescription = "location",
                    tint = Color(0xFF00B4D8)
                )
            }
        },
        supportingText = {
            Text("The location must be the arrival airport",
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
        },
        isError = (sharedViewModel.locationToRentCar == "" && sharedViewModel.buttonClickedCredentials)
    )
    //if there is an error with the arrival airport of the booking makes a text with the error
    if(carCredentialsViewModel.airportError) {
        Text(
            text = "Incorrect airport in this booking reference. Please check your details and try again.",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color.Red
        )
    }
    //text field for the date and time for pick up and return
    CarDatePickerDialog(
        sharedViewModel = sharedViewModel,
        carCredentialsViewModel = carCredentialsViewModel,
        pickUpOrReturn = 0
    )
    Row {
        Text("Pick Up Time:",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 12.dp, top = 35.dp),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            )
        )
        TimeDropdownMenu(
            sharedViewModel = sharedViewModel,
            carCredentialsViewModel = carCredentialsViewModel,
            hoursOrMins = 0,
            pickUpOrReturn = 0
        )
        TimeDropdownMenu(
            sharedViewModel = sharedViewModel,
            carCredentialsViewModel = carCredentialsViewModel,
            hoursOrMins = 1,
            pickUpOrReturn = 0
        )
    }
    CarDatePickerDialog(
        sharedViewModel = sharedViewModel,
        carCredentialsViewModel = carCredentialsViewModel,
        pickUpOrReturn = 1
    )
    Row {
        Text("Return Time:",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 12.dp, top = 35.dp),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            )
        )
        TimeDropdownMenu(
            sharedViewModel = sharedViewModel,
            carCredentialsViewModel = carCredentialsViewModel,
            hoursOrMins = 0,
            pickUpOrReturn = 1
        )
        TimeDropdownMenu(
            sharedViewModel = sharedViewModel,
            carCredentialsViewModel = carCredentialsViewModel,
            hoursOrMins = 1,
            pickUpOrReturn = 1
        )
    }
    //error if the return is same or before the pick up if the dates are the same
    if(sharedViewModel.returnDateCar != ""
        && sharedViewModel.pickUpDateCar == sharedViewModel.returnDateCar
        && sharedViewModel.pickUpHour >= sharedViewModel.returnHour) {
        Text(
            text = "Return time must be after pick up time if the pick up and return day is the same!",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 12.dp, top = 15.dp, end = 10.dp),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color.Red
        )
        carCredentialsViewModel.timeError = true
    }
    else{
        carCredentialsViewModel.timeError = false
    }
    //error if the rental datetime is not within the departure and return(if there is) flight in an arrival destination
    if(carCredentialsViewModel.rentingTimeError) {
        Text(
            text = "Τhe rental date and time must be within the limits of your flight!",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 12.dp, top = 15.dp, end = 10.dp),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color.Red
        )
    }
    //text field for the booking reference
    FlyNowTextField(
        text = sharedViewModel.textBookingId,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
        label = "Booking Reference",
        readOnly = false,
        onTextChange = {
            sharedViewModel.textBookingId = it
            sharedViewModel.buttonClickedCredentials = false
            carCredentialsViewModel.bookingError = false
        },
        leadingIcon = {
            Icon(
                Icons.Outlined.AirplaneTicket,
                contentDescription = "ticket",
                tint = Color(0xFF00B4D8)
            )
        },
        isError = ((sharedViewModel.textBookingId == ""
                && sharedViewModel.buttonClickedCredentials)
                || carCredentialsViewModel.bookingError)
    )
    //the text for the error of the booking reference
    if (carCredentialsViewModel.bookingError) {
        Text(
            text = "Incorrect booking reference. Please check your details and try again.",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 10.dp, top = 15.dp, end = 10.dp),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color.Red
        )
    }
}