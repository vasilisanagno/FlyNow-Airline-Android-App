package com.example.flynow.ui.screens.wifiDetails.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.wifiDetails.WifiDetailsViewModel

//component that shows the wifi radio buttons
@Composable
fun WifiRadioButtons(
    sharedViewModel: SharedViewModel,
    wifiDetailsViewModel: WifiDetailsViewModel
) {
    wifiDetailsViewModel.radioOptions.forEach { option ->
        Row(Modifier.fillMaxWidth()) {
            if (sharedViewModel.wifiInfo == 0) {
                RadioButton(
                    selected = (option == wifiDetailsViewModel.selectedOption),
                    onClick = {
                        wifiDetailsViewModel.selectedOption = option
                        if (option == wifiDetailsViewModel.radioOptions[0]) {
                            wifiDetailsViewModel.selectedWifi = 1
                            wifiDetailsViewModel.wifiPrice = 6.0
                        } else {
                            wifiDetailsViewModel.selectedWifi = 2
                            wifiDetailsViewModel.wifiPrice = 12.0
                        }
                    },
                    modifier = Modifier.padding(top = if (option == wifiDetailsViewModel.radioOptions[1]) 10.dp else 0.dp),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF00B4D8),
                        unselectedColor = Color(0xFF00B4D8)
                    )
                )
                Text(
                    text = option,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = if (option == wifiDetailsViewModel.radioOptions[1]) 22.dp else 12.dp),
                    color = Color(0xFF023E8A),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )
                )
            } else if (sharedViewModel.wifiInfo == 1
                && option == wifiDetailsViewModel.radioOptions[1]
            ) {
                RadioButton(
                    selected = (option == wifiDetailsViewModel.selectedOption),
                    onClick = {
                        wifiDetailsViewModel.selectedOption = option
                        wifiDetailsViewModel.selectedWifi = 2
                        wifiDetailsViewModel.wifiPrice = 6.0
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF00B4D8),
                        unselectedColor = Color(0xFF00B4D8)
                    )
                )
                Text(
                    text = wifiDetailsViewModel.radioOptions[1].substring(0, 78) + " 6€",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 12.dp),
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