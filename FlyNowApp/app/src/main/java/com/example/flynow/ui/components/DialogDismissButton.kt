package com.example.flynow.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel

//component that is used in FlyNowShowDialog and is the confirm button "no"
@Composable
fun DialogDismissButton(
    state: String,
    sharedViewModel: SharedViewModel
) {
    if (sharedViewModel.showDialog
        && ((state == "Wifi" && !sharedViewModel.updateWifi)||
                (state == "UpgradeClass" && !sharedViewModel.updateBusiness)||
                (state == "PetsFromMore" && !sharedViewModel.updatePets)||
                (state == "BaggageFromMore" && !sharedViewModel.updateBaggage))) {
        Button(
            onClick = {
                sharedViewModel.showDialog = false
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF023E8A)
            )
        ) {
            Text(
                "No",
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