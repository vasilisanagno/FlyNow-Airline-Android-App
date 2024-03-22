package com.example.flynow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flynow.R

//component that shows the bottom bar with a button "continue" to navigate to the next screens
//and the total price each time
@Composable
fun FlyNowBottomAppBar(
    navController: NavController,
    prepareForTheNextScreen: () -> Boolean,
    previousRoute: String,
    nextRoute: String,
    totalPrice: Double,
    enabled: Boolean
) {
    BottomAppBar(backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
        modifier = Modifier
            .height(60.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                "Total Price: $totalPrice €",
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                color = Color(0xFF023E8A),
                modifier = Modifier.padding(start = 5.dp)
            )
            //Button "Continue" that navigates the user to the next step of the reservation
            Button(
                onClick = {
                    val checkForInputs = prepareForTheNextScreen()
                    if(checkForInputs) {
                        //navigates to passenger page
                        navController.navigate(nextRoute) {
                            popUpTo(previousRoute)
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier
                    .padding(start = 45.dp)
                    .width(130.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00B4D8),
                    disabledContentColor = Color(0xFF023E8A)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 5.dp
                ),
                enabled = enabled
            ) {
                Text(
                    text = "Continue",
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