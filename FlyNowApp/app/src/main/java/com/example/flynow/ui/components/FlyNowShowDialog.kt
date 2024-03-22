package com.example.flynow.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageFromMoreDetails.BaggageDetailsViewModel
import com.example.flynow.ui.screens.petsFromMoreDetails.PetsDetailsViewModel
import com.example.flynow.ui.screens.upgradeClassDetails.UpgradeClassDetailsViewModel
import com.example.flynow.ui.screens.wifiDetails.WifiDetailsViewModel

//component that shows the alert dialog that is shown in the end
// for the completion of the update query and after that
//has an ok button that returns the user to the home page
@Composable
fun FlyNowShowDialog(
    state: String,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    wifiDetailsViewModel: WifiDetailsViewModel?,
    upgradeClassDetailsViewModel: UpgradeClassDetailsViewModel?,
    baggageDetailsViewModel: BaggageDetailsViewModel?,
    petsDetailsViewModel: PetsDetailsViewModel?
) {
    if (sharedViewModel.showDialog || sharedViewModel.showDialogConfirm) {
        Box(contentAlignment = Alignment.Center) {
            AlertDialog(
                onDismissRequest = { sharedViewModel.showDialog = false },
                title = {
                    DialogTitle(
                        state = state,
                        sharedViewModel = sharedViewModel
                    )
                },
                text = {
                    DialogText(
                        state = state,
                        sharedViewModel = sharedViewModel
                    )
                },
                confirmButton = {
                    DialogConfirmButton(
                        state = state,
                        navController = navController,
                        sharedViewModel = sharedViewModel,
                        wifiDetailsViewModel = wifiDetailsViewModel,
                        upgradeClassDetailsViewModel = upgradeClassDetailsViewModel,
                        baggageDetailsViewModel = baggageDetailsViewModel,
                        petsDetailsViewModel = petsDetailsViewModel
                    )
                },
                dismissButton = {
                    DialogDismissButton(
                        state = state,
                        sharedViewModel = sharedViewModel
                    )
                },
                containerColor = Color(0xFFEBF2FA),
                textContentColor = Color(0xFF023E8A),
                titleContentColor = Color(0xFF023E8A),
                tonalElevation = 30.dp,
                properties = DialogProperties(dismissOnClickOutside = false)
            )
        }
    }
}