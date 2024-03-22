package com.example.flynow.ui.screens.petsFromMoreDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.example.flynow.navigation.PetsFromMore
import com.example.flynow.navigation.PetsFromMoreDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel
import com.example.flynow.ui.screens.baggageAndPets.components.ShowPetField
import com.example.flynow.ui.screens.petsFromMoreDetails.PetsDetailsViewModel
import com.example.flynow.utils.Constants

//component that uses the component of the baggage and pets screen
@Composable
fun UpgradePetInReservation(
    navController: NavController,
    petsDetailsViewModel: PetsDetailsViewModel,
    baggageAndPetsViewModel: BaggageAndPetsViewModel,
    sharedViewModel: SharedViewModel
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
                    val petSize = sharedViewModel.petSize
                    petsDetailsViewModel.initializeVariables()
                    sharedViewModel.petSize = petSize
                    sharedViewModel.selectedIndex = 3
                    navController.navigate(PetsFromMore.route) {
                        popUpTo(PetsFromMoreDetails.route)
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
                    text = "Pets",
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
                    Icons.Outlined.Pets,
                    contentDescription = "UpgradePet",
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
            ShowPetField(
                state = "PetsFromMore",
                baggageAndPetsViewModel = baggageAndPetsViewModel,
                sharedViewModel = sharedViewModel
            )
            petsDetailsViewModel.petsPrice = sharedViewModel.tempPetPrice.toDouble()
        }
    }
}