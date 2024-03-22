package com.example.flynow.ui.screens.airports.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.model.Airport
import com.example.flynow.navigation.Airports
import com.example.flynow.navigation.Book
import com.example.flynow.navigation.CarCredentials
import com.example.flynow.navigation.Home
import com.example.flynow.ui.SharedViewModel

//component that shows the list of airports with a lazy column in airports screen
@Composable
fun ListOfAirports(
    navController: NavController,
    airports: List<Airport>,
    sharedViewModel: SharedViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(airports) { airportItem ->
            if(sharedViewModel.airportFrom != airportItem.name && sharedViewModel.airportTo != airportItem.name) {
                //airport texts that are clickable and when are clicked
                //go back to previous page the selected airport, in "To" destination
                //there is also the selection of keyword everywhere
                ClickableText(
                    onClick = {
                        if(!sharedViewModel.rentCar) {
                            if (sharedViewModel.whatAirport == 0) {
                                sharedViewModel.airportFrom = airportItem.name
                            } else if (sharedViewModel.whatAirport == 1) {
                                sharedViewModel.airportTo = airportItem.name
                            }
                            if (sharedViewModel.selectedIndex == 0) {
                                navController.navigate(Home.route) {
                                    popUpTo(Airports.route)
                                    launchSingleTop = true
                                }
                            } else if (sharedViewModel.selectedIndex == 1) {
                                navController.navigate(Book.route) {
                                    popUpTo(Airports.route)
                                    launchSingleTop = true
                                }
                            }
                        }
                        else {
                            sharedViewModel.locationToRentCar = airportItem.name
                            navController.navigate(CarCredentials.route) {
                                popUpTo(Airports.route)
                                launchSingleTop = true
                            }
                        }
                    },
                    text = AnnotatedString(
                        "${airportItem.city} (${airportItem.name})"),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF023E8A),
                        textIndent = TextIndent(20.sp, 0.sp)
                    )
                )
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color(0xFF023E8A)
                )
            }
        }
    }
}