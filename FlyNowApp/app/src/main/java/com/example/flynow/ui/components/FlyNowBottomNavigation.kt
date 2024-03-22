package com.example.flynow.ui.components

import androidx.animation.LinearEasing
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.flynow.MainViewModel
import com.example.flynow.R
import com.example.flynow.navigation.Book
import com.example.flynow.navigation.Home
import com.example.flynow.navigation.More
import com.example.flynow.navigation.MyBooking
import com.example.flynow.ui.SharedViewModel

//component that removes or add the bottom navigation and contains
//four categories "Home", "Book", "My Booking", "More", each navigates to another page.
//In bottom bar each text contains and an descriptive icon
@Composable
fun FlyNowBottomNavigation(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    sharedViewModel: SharedViewModel
) {
    val destinationList = listOf(
        Home,
        Book,
        MyBooking,
        More
    )

    AnimatedVisibility(visible = mainViewModel.bottomBarState,
        enter = expandVertically(animationSpec = tween(durationMillis = 800,
            easing = LinearEasing
        ), expandFrom = Alignment.Bottom),
        exit = shrinkVertically(animationSpec = tween(durationMillis = 800,
            easing = LinearEasing
        ), shrinkTowards = Alignment.Top),
        content = {
            BottomNavigation(
                modifier = Modifier.size(450.dp, 57.dp),
                backgroundColor = Color(0xFF0096C7)
            ) {
                destinationList.forEachIndexed { index, destination ->
                    BottomNavigationItem(
                        selected = index == sharedViewModel.selectedIndex,
                        label = {
                            Text(
                                text = destination.title,
                                color = if (sharedViewModel.selectedIndex == index) Color.White else Color(
                                    0xAAFFFFFF
                                ),
                                fontSize = 12.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                ),
                                softWrap = false
                            )
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = destination.icon),
                                contentDescription = destination.title,
                                tint = if (sharedViewModel.selectedIndex == index) Color.White else Color(
                                    0xAAFFFFFF
                                ),
                                modifier = Modifier.size(27.dp)
                            )
                        },
                        onClick = {
                            sharedViewModel.selectedIndex = index
                            if(index == 0 || index == 1) {
                                sharedViewModel.passengersCounter = 1
                            }
                            if(index != 2) {
                                sharedViewModel.textBookingId = ""
                                sharedViewModel.textLastname = ""
                                sharedViewModel.hasError = false
                                sharedViewModel.buttonClickedCredentials = false
                            }
                            navController.navigate(destination.route) {
                                popUpTo(Home.route)
                                launchSingleTop = true
                            }
                        },
                        selectedContentColor = Color(0xFFADE8F4)
                    )
                }
            }
        }
    )
}