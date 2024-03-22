package com.example.flynow.ui.screens.cars

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
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
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowBottomAppBar
import com.example.flynow.ui.screens.cars.components.CarsList
import com.example.flynow.ui.screens.cars.components.ShowCarDialog
import com.example.flynow.utils.Constants
import kotlinx.coroutines.delay

//screen that shows the available cars to rent according the user choices
//navController helps to navigate to previous page or next page,
//car credentials view model is the view model that keeps the variables of this screen
//and communicates with the server
//and shared view model is for shared variables across the app
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CarsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    carsViewModel: CarsViewModel
) {
    //api that insert the renting of car
    LaunchedEffect(carsViewModel.insertNewBookingOfCar) {
        if(carsViewModel.insertNewBookingOfCar) {
            carsViewModel.rentACar()
            delay(3000)
            carsViewModel.insertNewBookingOfCar = false
        }
    }
    //delay 3 seconds to be the progress bar and launch the flights
    LaunchedEffect(Unit) {
        if(sharedViewModel.showProgressBar) {
            delay(3000)
            sharedViewModel.showProgressBar = false
        }
    }
    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        if(sharedViewModel.seeBottomBar) {
            FlyNowBottomAppBar(
                navController = navController,
                prepareForTheNextScreen = { carsViewModel.finishInsertCar() },
                previousRoute = "",
                nextRoute = "",
                totalPrice = carsViewModel.totalPriceForCar*sharedViewModel.daysDifference,
                enabled = carsViewModel.totalPriceForCar != 0.0
            )
        }
    }) {
        ShowCarDialog(
            navController = navController,
            sharedViewModel = sharedViewModel,
            carsViewModel = carsViewModel
        )
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    //clicking the back icon to go back in the previous page
                    //initializes to the original values of all variables
                    carsViewModel.initializeVariables()
                    //navigates back to the book page
                    navController.navigate(CarCredentials.route) {
                        popUpTo(Cars.route)
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
                        text = "Cars",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Constants.gradient)
            ) {
                if(!sharedViewModel.showProgressBar) {
                    CarsList(
                        navController = navController,
                        sharedViewModel = sharedViewModel,
                        carsViewModel = carsViewModel
                    )
                }
                else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(bottom = 100.dp),
                            color = Color(0xFF023E8A)
                        )
                    }
                }
            }
        }
    }
}