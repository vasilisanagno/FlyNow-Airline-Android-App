package com.example.flynow.ui.screens.cars.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.CarCredentials
import com.example.flynow.navigation.Cars
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.cars.CarsViewModel

//component that shows when cars not found according to the user choices
@Composable
fun NoCarResults(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    carsViewModel: CarsViewModel
) {
    if (sharedViewModel.listOfCars.size == 0) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 250.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "No Cars Found!",
                fontSize = 20.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                color = Color(0xFF023E8A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            Button(
                onClick = {
                    //clicking the back icon to go back in the previous page
                    //initializes to the original values of all variables
                    carsViewModel.initializeVariables()
                    navController.navigate(CarCredentials.route) {
                        popUpTo(Cars.route)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .width(140.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00B4D8),
                    disabledContentColor = Color(0xFF023E8A)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 5.dp
                )
            ) {
                Text(
                    "Go Back",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF023E8A),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}