package com.example.flynow.ui.screens.wifiDetails.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.Wifi
import com.example.flynow.navigation.WifiDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.wifiDetails.WifiDetailsViewModel
import com.example.flynow.utils.Constants

//component that shows the radio buttons with the options of
// wifi package for the user to choose
@Composable
fun AddWifiOnBoard(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    wifiDetailsViewModel: WifiDetailsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //back button and the title of the screen
            IconButton(
                onClick = {
                    wifiDetailsViewModel.initializeVariables()
                    sharedViewModel.selectedIndex = 3
                    navController.navigate(Wifi.route) {
                        popUpTo(WifiDetails.route)
                        launchSingleTop = true
                    }
                }
            )
            {
                Icon(
                    Icons.Outlined.ArrowBackIos,
                    contentDescription = "back",
                    tint = Color(0xFF023E8A)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Wifi οn Board",
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
                    Icons.Outlined.Wifi,
                    contentDescription = "WifiOnBoard",
                    modifier = Modifier.padding(start = 5.dp, top = 2.dp, end = 40.dp),
                    tint = Color(0xFF023E8A)
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
        Column(
            Modifier
                .fillMaxSize()
                .background(Constants.gradient)
        ) {
            Image(
                painter = painterResource(id = R.drawable.wifionboard),
                contentDescription = "wifi",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 2.6f)
                    .padding(start = 10.dp, top = 5.dp, end = 10.dp)
            )
            Text(
                text = "Choose the Wifi package that suits you!",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
            if (sharedViewModel.wifiInfo != 2) {
                WifiRadioButtons(
                    sharedViewModel = sharedViewModel,
                    wifiDetailsViewModel = wifiDetailsViewModel
                )
            } else {
                Text(
                    text = "You have already selected Audio/Video streaming, \nHigh speed Web Browsing & Social Media, \nup to 15Mbps!",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 12.dp),
                    color = Color(0xFF023E8A),
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