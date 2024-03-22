package com.example.flynow.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.home.components.HomeCard

//This is the first (Home) screen that is displayed from the app, where user can search for a flight
//or navigate to other functionalities of the app using the bottom navigation bar
//Function that creates the home screen
//navController helps to navigate to previous page or next page,
//sharedViewModel data that are useful in this screen
@Composable
fun HomeScreen(navController: NavController,
               sharedViewModel: SharedViewModel
) {
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
            HomeCard(navController, sharedViewModel)
        }
    }
}