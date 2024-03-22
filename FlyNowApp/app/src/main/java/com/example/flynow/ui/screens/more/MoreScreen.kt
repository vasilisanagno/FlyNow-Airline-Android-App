package com.example.flynow.ui.screens.more

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.flynow.navigation.BaggageFromMore
import com.example.flynow.navigation.CarCredentials
import com.example.flynow.navigation.CheckIn
import com.example.flynow.navigation.MyBooking
import com.example.flynow.navigation.PetsFromMore
import com.example.flynow.navigation.UpgradeClass
import com.example.flynow.navigation.Wifi
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.more.components.CardOption
import com.example.flynow.ui.screens.more.components.ListOption
import com.example.flynow.utils.Constants

//screen that shows the more and has two parameters navController and selectedIndex
//because there is a bottom bar in this screen
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MoreScreen(navController: NavController,
               sharedViewModel: SharedViewModel,
               moreViewModel: MoreViewModel,) {
    //removes the card focus when is clicked

    Column(modifier = Modifier
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "More",
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
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
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Constants.gradient)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                //Card for "Check-In" and goes to "Check-In" when it is clicked
                CardOption(
                    navController = navController,
                    interactionSource = moreViewModel.interactionSource,
                    highlightIndication = moreViewModel.highlightIndication,
                    title = "Check-In",
                    icon = Icons.Filled.FactCheck,
                    route = CheckIn.route,
                    sharedViewModel = sharedViewModel
                )
                //Card for "Change your Booking" and goes to "My Booking" when it is clicked
                CardOption(
                    navController = navController,
                    interactionSource = moreViewModel.interactionSource,
                    highlightIndication = moreViewModel.highlightIndication,
                    title = "Change your Booking",
                    icon = Icons.Filled.AirplaneTicket,
                    route = MyBooking.route,
                    sharedViewModel = sharedViewModel
                )
            }
            //a surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0f)
            ) {
                //list of selections in the more screen
                val textList = listOf("Upgrade to Business Class",
                    "Extra baggage", "Travelling with pets", "Rent a car",
                    "Upgrade wifi on board")
                val routesList = listOf(UpgradeClass.route,  BaggageFromMore.route,
                    PetsFromMore.route, CarCredentials.route, Wifi.route)
                LazyColumn(modifier = Modifier.padding(top = 92.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    items(textList.size) { pos ->
                        //these selections are clickable text and
                        ListOption(
                            title = textList[pos],
                            navController = navController,
                            route = routesList[pos]
                        )
                    }
                }
            }
        }
    }
}



