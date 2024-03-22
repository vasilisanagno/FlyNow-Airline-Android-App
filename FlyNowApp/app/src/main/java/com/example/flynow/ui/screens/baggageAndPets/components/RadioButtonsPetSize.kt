package com.example.flynow.ui.screens.baggageAndPets.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel

//radio buttons for sizes of pets
@Composable
fun radioButtonsPetSize(
    onChange: (String) -> Unit,
    baggageAndPetsViewModel: BaggageAndPetsViewModel?,
    sharedViewModel: SharedViewModel
): String {
    var radioOptions: List<String> = emptyList()
    when (sharedViewModel.petSize) {
        "" -> {
            radioOptions = listOf("Small (<8kg) - 35€","Medium (<25kg) - 50€", "Large (>25kg) - 90€")
        }
        "Small" -> {
            radioOptions = listOf("Medium (<25kg) - 15€", "Large (>25kg) - 55€")
        }
        "Medium" -> {
            radioOptions = listOf("Large (>25kg) - 40€")
        }
    }

    Column(Modifier.fillMaxWidth()) {
        radioOptions.forEach { option ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (option == baggageAndPetsViewModel!!.selectedOptionForYes),
                    onClick = { baggageAndPetsViewModel.selectedOptionForYes = option
                        onChange(baggageAndPetsViewModel.selectedOptionForYes)
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF00B4D8),
                        unselectedColor = Color(0xFF00B4D8)
                    )
                )
                Text(
                    text = option,
                    fontSize = 16.sp,
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
    return baggageAndPetsViewModel!!.selectedOptionForYes
}