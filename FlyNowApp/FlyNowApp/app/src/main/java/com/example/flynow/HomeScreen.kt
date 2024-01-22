//This is the first (Home) screen that is displayed from the app, where user can search for a flight
//or navigate to other functionalities of the app using the bottom navigation bar
package com.example.flynow

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController

//Function that creates the home screen
//navController helps to navigate to previous page or next page,
//selectedIndex helps if there is the bottom bar in previous page
//to go in the correct icon of the bottom bar,
//airportFrom and airportTo is in what variable will be stored the airport that is selected,
//whatAirport shows if the click from previous page has come from
//airportTo or airportFrom, rentCar shows if the previous page was "rent a car"
@Composable
fun HomeScreen(navController: NavController,
               pageNow: MutableIntState,
               selectedIndex: MutableState<Int>,
               airportFrom: MutableState<String>,
               airportTo: MutableState<String>,
               whatAirport: MutableIntState,
               rentCar: MutableState<Boolean>) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painterResource(id = R.drawable.airplane),
            contentDescription = "BackgroundImage",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        //Display app logo at the top of the screen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "FlyNow",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color(0xFFF5F9FF),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                modifier = Modifier.padding(start = 50.dp, top = 15.dp)
            )
            Image(
                painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier
                    .size(80.dp, 80.dp)
                    .padding(top = 15.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Where do you want to fly?",
                modifier = Modifier
                    .padding(bottom = 280.dp)
                    .shadow(elevation = 40.dp),
                fontSize = 25.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                color = Color(0xFF023E8A),
                textAlign = TextAlign.Center
            )
            //Display the card view for the flight search with the "From" and "To" text input fields
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
                    OutlinedTextField(
                        value = airportFrom.value,
                        onValueChange = { airportFrom.value = it },
                        modifier = Modifier.fillMaxWidth().
                        clickable(enabled = false,onClickLabel = null, onClick = {}),
                        label = { Text("From", fontSize = 16.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedLabelColor = Color(0xFF023E8A),
                            focusedBorderColor = Color(0xFF023E8A),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFF00B4D8)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        leadingIcon = {
                            //selecting airport about clicking the icon
                            IconButton(onClick = {
                                whatAirport.intValue = 0
                                rentCar.value = false
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
                        },
                        textStyle = TextStyle.Default.copy(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            )
                        ),
                        readOnly = true
                    )
                    //Text input "To"
                    OutlinedTextField(
                        value = airportTo.value,
                        onValueChange = { airportTo.value = it },
                        modifier = Modifier.fillMaxWidth().clickable(enabled = false,onClickLabel = null, onClick = {}),
                        label = { Text("To", fontSize = 16.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedLabelColor = Color(0xFF023E8A),
                            focusedBorderColor = Color(0xFF023E8A),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFF00B4D8)
                        ),
                        singleLine = true,
                        leadingIcon = {
                            //selecting airport about clicking the icon
                            IconButton(onClick = {
                                whatAirport.intValue = 1
                                rentCar.value = false
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
                        },
                        textStyle = TextStyle.Default.copy(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            )
                        ),
                        readOnly = true
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        //Button "Book A Flight" that navigates the user to the "Book" screen
                        ElevatedButton(
                            onClick = {
                                //go to Book Screen
                                selectedIndex.value = 1
                                pageNow.intValue = 0
                                navController.navigate(Book.route) {
                                    popUpTo(Home.route)
                                    launchSingleTop = true
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(top = 10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 10.dp
                            )
                        ) {
                            Text(text = "Book A Flight",
                                fontSize = 18.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}