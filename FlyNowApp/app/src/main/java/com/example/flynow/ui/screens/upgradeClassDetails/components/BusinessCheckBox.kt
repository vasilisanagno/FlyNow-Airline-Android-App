package com.example.flynow.ui.screens.upgradeClassDetails.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.upgradeClassDetails.UpgradeClassDetailsViewModel

//component for the checkbox to select the type of upgrade the user wants
@Composable
fun BusinessCheckBox(
    sharedViewModel: SharedViewModel,
    upgradeClassDetailsViewModel: UpgradeClassDetailsViewModel,
    index: Int
) {
    var checkBoxState by remember {
        mutableStateOf(false)
    }
    if (index == 0) {
        Text(
            text = "Live the Business Class experience now!",
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
    }
    Text(
        text = if (index == 0) "Outbound" else "Inbound",
        fontSize = 20.sp,
        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
        fontFamily = FontFamily(
            fonts = listOf(
                Font(
                    resId = R.font.opensans
                )
            )
        ),
        color = Color(0xFF023E8A),
        fontWeight = FontWeight.Bold
    )
    Text(
        text =
        when (sharedViewModel.upgradeToBusinessInfo[index]) {
            "BUSINESS CLASS" -> "Your flight is already in Business Class."
            "FLEX CLASS" -> "Your flight is in Flex Class."
            else -> "Your flight is in Economy Class."
        },
        fontSize = 16.sp,
        modifier = Modifier.padding(start = 10.dp, top = 15.dp),
        fontFamily = FontFamily(
            fonts = listOf(
                Font(
                    resId = R.font.opensans
                )
            )
        ),
        color = Color(0xFF023E8A)
    )
    if (sharedViewModel.upgradeToBusinessInfo[index] != "BUSINESS CLASS") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkBoxState,
                onCheckedChange = {
                    checkBoxState = it
                    if (checkBoxState) {
                        upgradeClassDetailsViewModel.upgradeBusinessPrice += 55.0
                        upgradeClassDetailsViewModel.selectedUpgradeBusiness[index] = true
                    } else {
                        upgradeClassDetailsViewModel.upgradeBusinessPrice -= 55.0
                        upgradeClassDetailsViewModel.selectedUpgradeBusiness[index] = false
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF00B4D8),
                    uncheckedColor = Color(0xFF00B4D8)
                )
            )
            Text(
                text = "Upgrade to Business Class - 55€",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 10.dp),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                color = Color(0xFF023E8A)
            )
        }
    }
}