package com.example.flynow.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.Airports
import com.example.flynow.navigation.Book
import com.example.flynow.navigation.Home
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowButton
import com.example.flynow.ui.components.FlyNowTextField

//Display the card view for the flight search with the "From" and "To" text input fields
@Composable
fun HomeCard(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .height(250.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 9.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .padding(30.dp)
                .fillMaxSize()
        ) {
            //Text input "From"
            FlyNowTextField(
                text = sharedViewModel.airportFrom,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false, onClickLabel = null, onClick = {}),
                label = "From",
                readOnly = true,
                onTextChange = { sharedViewModel.airportFrom = it },
                leadingIcon = {
                    //selecting airport about clicking the icon
                    IconButton(onClick = {
                        sharedViewModel.whatAirport = 0
                        sharedViewModel.rentCar = false
                        navController.navigate(Airports.route) {
                            popUpTo(Home.route)
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            painterResource(id = R.drawable.takeoff),
                            contentDescription = "takeOff",
                            tint = Color(0xFF00B4D8)
                        )
                    }
                }
            )
            //Text input "To"
            FlyNowTextField(
                text = sharedViewModel.airportTo,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false, onClickLabel = null, onClick = {}),
                label = "To",
                readOnly = true,
                onTextChange = { sharedViewModel.airportTo = it },
                leadingIcon = {
                    //selecting airport about clicking the icon
                    IconButton(onClick = {
                        sharedViewModel.whatAirport = 1
                        sharedViewModel.rentCar = false
                        navController.navigate(Airports.route) {
                            popUpTo(Home.route)
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            painterResource(id = R.drawable.landon),
                            contentDescription = "landon",
                            tint = Color(0xFF00B4D8)
                        )
                    }
                }
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                //Button "Book A Flight" that navigates the user to the "Book" screen
                FlyNowButton(
                    text = "Book A Flight",
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {
                        //go to Book Screen
                        sharedViewModel.selectedIndex = 1
                        sharedViewModel.page = 0
                        navController.navigate(Book.route) {
                            popUpTo(Home.route)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}