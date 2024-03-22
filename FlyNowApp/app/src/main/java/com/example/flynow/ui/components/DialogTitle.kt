package com.example.flynow.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel

//component that is used in FlyNowShowDialog and is the title in the dialog
@Composable
fun DialogTitle(
    state: String,
    sharedViewModel: SharedViewModel
) {
    Text(
        text =
        if(state == "Wifi") {
            if (sharedViewModel.showDialog)
                "Confirm Updating Wifi"
            else if(!sharedViewModel.updateWifi)
                "The Update Of Wifi Was Done Successfully!"
            else ""
        }
        else if(state == "UpgradeClass") {
            if (sharedViewModel.showDialog)
                "Confirm Upgrading Class"
            else if(!sharedViewModel.updateBusiness)
                "The Upgrade Of Class Was Done Successfully!"
            else ""
        }
        else if(state == "PetsFromMore") {
            if (sharedViewModel.showDialog)
                "Confirm Addition Of Pet"
            else if(!sharedViewModel.updatePets)
                "The Addition Of Pet Was Done Successfully!"
            else ""
        }
        else if(state == "BaggageFromMore") {
            if (sharedViewModel.showDialog)
                "Confirm Addition Of Baggage"
            else if(!sharedViewModel.updateBaggage)
                "The Addition Of Baggage Was Done Successfully!"
            else ""
        }
        else {""},
        fontSize = 20.sp,
        fontFamily = FontFamily(
            fonts = listOf(
                Font(
                    resId = R.font.opensans
                )
            )
        ),
        fontWeight = FontWeight.Bold
    )
    if(state == "Wifi" && !sharedViewModel.updateWifi) {
        Icon(
            if (sharedViewModel.showDialog) Icons.Filled.QuestionMark
            else Icons.Filled.Verified,
            contentDescription = "question",
            modifier =
            if (sharedViewModel.showDialog) Modifier.padding(start = 205.dp)
            else Modifier.padding(top = 33.dp, start = 175.dp),
            tint = Color(0xFF023E8A)
        )
    }
    else if(state == "UpgradeClass" && !sharedViewModel.updateBusiness) {
        Icon(
            if (sharedViewModel.showDialog) Icons.Filled.QuestionMark
            else Icons.Filled.Verified,
            contentDescription = "question",
            modifier =
            if (sharedViewModel.showDialog) Modifier.padding(start = 227.dp)
            else Modifier.padding(top = 33.dp, start = 175.dp),
            tint = Color(0xFF023E8A)
        )
    }
    else if(state == "PetsFromMore" && !sharedViewModel.updatePets) {
        Icon(
            if (sharedViewModel.showDialog) Icons.Filled.QuestionMark
            else Icons.Filled.Verified,
            contentDescription = "question",
            modifier =
            if (sharedViewModel.showDialog) Modifier.padding(start = 224.dp)
            else Modifier.padding(top = 33.dp, start = 175.dp),
            tint = Color(0xFF023E8A)
        )
    }
    else if(state == "BaggageFromMore" && !sharedViewModel.updateBaggage) {
        Icon(
            if (sharedViewModel.showDialog) Icons.Filled.QuestionMark
            else Icons.Filled.Verified,
            contentDescription = "question",
            modifier =
            if (sharedViewModel.showDialog) Modifier.padding(top = 33.dp, start = 75.dp)
            else Modifier.padding(top = 33.dp, start = 225.dp),
            tint = Color(0xFF023E8A)
        )
    }
}