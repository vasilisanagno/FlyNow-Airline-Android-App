package com.example.flynow.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.BaggageFromMoreDetails
import com.example.flynow.navigation.Home
import com.example.flynow.navigation.PetsFromMoreDetails
import com.example.flynow.navigation.UpgradeClassDetails
import com.example.flynow.navigation.WifiDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageFromMoreDetails.BaggageDetailsViewModel
import com.example.flynow.ui.screens.petsFromMoreDetails.PetsDetailsViewModel
import com.example.flynow.ui.screens.upgradeClassDetails.UpgradeClassDetailsViewModel
import com.example.flynow.ui.screens.wifiDetails.WifiDetailsViewModel

//component that is used in FlyNowShowDialog and is the confirm button "yes" or "ok"
@Composable
fun DialogConfirmButton(
    state: String,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    wifiDetailsViewModel: WifiDetailsViewModel?,
    upgradeClassDetailsViewModel: UpgradeClassDetailsViewModel?,
    baggageDetailsViewModel: BaggageDetailsViewModel?,
    petsDetailsViewModel: PetsDetailsViewModel?
) {
    if (sharedViewModel.showDialog && ((state == "Wifi" && !sharedViewModel.updateWifi)
                ||(state == "UpgradeClass" && !sharedViewModel.updateBusiness)
                ||(state == "PetsFromMore" && !sharedViewModel.updatePets)
                ||(state == "BaggageFromMore" && !sharedViewModel.updateBaggage))) {
        Button(
            onClick = {
                when (state) {
                    "Wifi" -> {
                        sharedViewModel.updateWifi = true
                    }
                    "UpgradeClass" -> {
                        sharedViewModel.updateBusiness = true
                    }
                    "PetsFromMore" -> {
                        sharedViewModel.updatePets = true
                    }
                    "BaggageFromMore" -> {
                        sharedViewModel.updateBaggage = true
                    }
                }
                sharedViewModel.showDialog = false
                sharedViewModel.showDialogConfirm = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF023E8A)
            )
        ) {
            Text(
                "Yes",
                fontSize = 16.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
        }
    } else {
        if((state == "Wifi" && !sharedViewModel.updateWifi)
            ||(state == "UpgradeClass" && !sharedViewModel.updateBusiness)
            ||(state == "PetsFromMore" && !sharedViewModel.updatePets)
            ||(state == "BaggageFromMore" && !sharedViewModel.updateBaggage)) {
            Button(
                onClick = {
                    sharedViewModel.showDialogConfirm = false
                    sharedViewModel.showDialog = false
                    when (state) {
                        "Wifi" -> {
                            wifiDetailsViewModel!!.initializeVariables()
                        }
                        "UpgradeClass" -> {
                            upgradeClassDetailsViewModel!!.initializeVariables()
                        }
                        "BaggageFromMore" -> {
                            baggageDetailsViewModel!!.initializeVariables()
                        }
                        "PetsFromMore" -> {
                            petsDetailsViewModel!!.initializeVariables()
                        }
                    }
                    when (state) {
                        "Wifi" -> {
                            navController.navigate(Home.route) {
                                popUpTo(WifiDetails.route)
                                launchSingleTop = true
                            }
                        }
                        "UpgradeClass" -> {
                            navController.navigate(Home.route) {
                                popUpTo(UpgradeClassDetails.route)
                                launchSingleTop = true
                            }
                        }
                        "PetsFromMore" -> {
                            navController.navigate(Home.route) {
                                popUpTo(PetsFromMoreDetails.route)
                                launchSingleTop = true
                            }
                        }
                        "BaggageFromMore" -> {
                            navController.navigate(Home.route) {
                                popUpTo(BaggageFromMoreDetails.route)
                                launchSingleTop = true
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF023E8A)
                )
            ) {
                Text(
                    "OK",
                    fontSize = 16.sp,
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