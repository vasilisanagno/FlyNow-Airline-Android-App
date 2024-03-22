package com.example.flynow.ui.screens.airports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalAirport
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.Airports
import com.example.flynow.navigation.Book
import com.example.flynow.navigation.CarCredentials
import com.example.flynow.navigation.Home
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowTextField
import com.example.flynow.ui.screens.airports.components.ListOfAirports
import com.example.flynow.utils.Constants

//In this screen the user of the app can search for an airport
//airports screen with the textfield and the list of the airports to select
//navController helps to navigate to previous page,
//sharedViewModel data that are useful in this screen
//and data from airportViewModel
@Composable
fun AirportsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {

    val airportViewModel = hiltViewModel<AirportViewModel>()
    val searchText by airportViewModel.searchText.collectAsState()
    val airports by airportViewModel.airports.collectAsState()
    val isSearching by airportViewModel.isSearching.collectAsState()
    //variables that help in searching textfield

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //back button to go back to previous page
            IconButton(onClick = {
                if(!sharedViewModel.rentCar) {
                    //if the selection is from home of the airport returns to home
                    if (sharedViewModel.selectedIndex == 0) {
                        navController.navigate(Home.route) {
                            popUpTo(Airports.route)
                            launchSingleTop = true
                        }
                    }
                    //if the selection is from book of the airport returns to book
                    else if (sharedViewModel.selectedIndex == 1) {
                        navController.navigate(Book.route) {
                            popUpTo(Airports.route)
                            launchSingleTop = true
                        }
                    }
                }
                //if the selection is from rent a car of the airport returns to rent a car
                else {
                    navController.navigate(CarCredentials.route) {
                        popUpTo(Airports.route)
                        launchSingleTop = true
                    }
                }
            }) {
                Icon(
                    Icons.Outlined.ArrowBackIos,
                    contentDescription = "back",
                    tint = Color(0xFF023E8A)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                //title of the page
                Text(
                    text = "Select Airport",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(end = 45.dp),
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
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Constants.gradient),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //textfield to insert the airport that you want
            FlyNowTextField(
                text = searchText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 10.dp, end = 10.dp),
                label = "Airport",
                readOnly = false,
                onTextChange = airportViewModel::onSearchTextChange,
                leadingIcon = {
                    Icon(
                        Icons.Filled.LocalAirport,
                        contentDescription = "airport",
                        tint = Color(0xFF00B4D8)
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done))
            //when searching from typing letters shows a circular progress
            //otherwise the list of the result of searching
            if (isSearching) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 200.dp),
                        color = Color(0xFF023E8A)
                    )
                }
            } else {
                ListOfAirports(
                    navController = navController,
                    airports = airports,
                    sharedViewModel = sharedViewModel
                )
            }
        }
    }
}