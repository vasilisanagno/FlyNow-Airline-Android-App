package com.example.flynow.ui.screens.baggageAndPets.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel

//radio buttons yes and no for pets
@Composable
fun radioButtonsPetYesNo(
    baggageAndPetsViewModel: BaggageAndPetsViewModel?
): String {
    Row {
        baggageAndPetsViewModel!!.radioOptions.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (option == baggageAndPetsViewModel.selectedOption),
                    onClick = { baggageAndPetsViewModel.selectedOption = option },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF00B4D8),
                        unselectedColor = Color(0xFF00B4D8)
                    )
                )
                Text(
                    text = option,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 5.dp),
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
    return baggageAndPetsViewModel!!.selectedOption
}