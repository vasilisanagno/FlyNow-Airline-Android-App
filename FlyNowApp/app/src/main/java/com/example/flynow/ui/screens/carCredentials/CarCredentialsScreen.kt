package com.example.flynow.ui.screens.carCredentials

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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.Divider
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
import com.example.flynow.navigation.CarCredentials
import com.example.flynow.navigation.Cars
import com.example.flynow.navigation.More
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowButton
import com.example.flynow.ui.screens.carCredentials.components.CarTextFields
import com.example.flynow.utils.Constants
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

//screen that shows the credentials to search the cars that the user maybe book
//navController helps to navigate to previous page or next page,
//car credentials view model is the view model that keeps the variables of this screen
//and communicates with the server
//and shared view model is for shared variables across the app
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CarCredentialsScreen(
    navController: NavController,
    carCredentialsViewModel: CarCredentialsViewModel,
    sharedViewModel: SharedViewModel
) {
    //api for checking if the booking exists and does not have any error
    //about airport, booking id and pick-up/return time of the car
    LaunchedEffect(carCredentialsViewModel.bookingExists) {
        if(carCredentialsViewModel.bookingExists) {
            carCredentialsViewModel.checkCredentials()
            delay(1000)
            //if is alright continue to searching cars query
            if(!carCredentialsViewModel.bookingError
                && !carCredentialsViewModel.airportError
                && !carCredentialsViewModel.rentingTimeError) {
                carCredentialsViewModel.searchCarsQuery = true
                carCredentialsViewModel.bookingExists = false
            }
            else {
                carCredentialsViewModel.bookingExists = false
            }
        }
    }
    //api that searching cars according to the info type the user
    //and storing them to the listOfCars list and checks the cars in
    //the specific date times that the user select not to be in some reservation
    LaunchedEffect(carCredentialsViewModel.searchCarsQuery) {
        if(carCredentialsViewModel.searchCarsQuery) {
            carCredentialsViewModel.getAvailableCars()
            sharedViewModel.showProgressBar = true
            carCredentialsViewModel.searchCarsQuery = false
            //navigates to the next page
            navController.navigate(Cars.route) {
                popUpTo(CarCredentials.route)
                launchSingleTop = true
            }
        }
    }
    //initializes the airport error when comes from airport screen
    LaunchedEffect(Unit) {
        carCredentialsViewModel.airportError = false
    }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                //back button that returns to more screen and initialization of the variables
                carCredentialsViewModel.initializeVariables()
                navController.navigate(More.route) {
                    popUpTo(CarCredentials.route)
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
                    text = "Rent A Car",
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
                    Icons.Outlined.DirectionsCar,
                    contentDescription = "cars",
                    modifier = Modifier.padding(start = 5.dp, end = 45.dp),
                    tint = Color(0xFF023E8A)
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Constants.gradient)
            .verticalScroll(rememberScrollState())) {
            CarTextFields(
                navController = navController,
                sharedViewModel = sharedViewModel,
                carCredentialsViewModel = carCredentialsViewModel
            )
            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                //button for continuing to the next page for the cars
                FlyNowButton(
                    text = "Search Cars",
                    modifier = Modifier.padding(top = 30.dp),
                    onClick = {
                        sharedViewModel.buttonClickedCredentials = true
                        if(sharedViewModel.locationToRentCar != "" &&
                            sharedViewModel.pickUpDateCar != "" &&
                            sharedViewModel.returnDateCar != "" &&
                            sharedViewModel.textBookingId != "" &&
                            !carCredentialsViewModel.timeError)
                        {
                            val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                            val date1 = LocalDate.parse(sharedViewModel.pickUpDateCar, dateFormat)
                            val date2 = LocalDate.parse(sharedViewModel.returnDateCar, dateFormat)

                            sharedViewModel.daysDifference = ChronoUnit.DAYS.between(date1, date2).toInt()
                            if(sharedViewModel.daysDifference == 0) {
                                sharedViewModel.daysDifference = 1
                            }
                            carCredentialsViewModel.bookingExists = true
                        }
                    }
                )
            }
        }
    }
}