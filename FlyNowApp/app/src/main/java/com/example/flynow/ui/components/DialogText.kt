package com.example.flynow.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel

//component that is used in FlyNowShowDialog and is the text in the dialog
@Composable
fun DialogText(
    state: String,
    sharedViewModel: SharedViewModel
) {
    if (sharedViewModel.showDialog && ((state == "Wifi" && !sharedViewModel.updateWifi)
                ||(state == "UpgradeClass" && !sharedViewModel.updateBusiness)
                ||(state == "PetsFromMore" && !sharedViewModel.updatePets)
                ||(state == "BaggageFromMore" && !sharedViewModel.updateBaggage))) {
        Text(
            text = when (state) {
                "Wifi" -> "Are you sure you want to update the wifi?"
                "UpgradeClass" -> "Are you sure you want to upgrade the class?"
                "PetsFromMore" -> "Are you sure you want to add a pet?"
                "BaggageFromMore" -> "Are you sure you want to add baggage?"
                else -> ""
            },
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
    if ((state == "Wifi" && sharedViewModel.updateWifi)
        ||(state == "UpgradeClass" && sharedViewModel.updateBusiness)
        ||(state == "PetsFromMore" && sharedViewModel.updatePets)
        ||(state == "BaggageFromMore" && sharedViewModel.updateBaggage)) {
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = Color(0xFF023E8A)
            )
        }
    }
}