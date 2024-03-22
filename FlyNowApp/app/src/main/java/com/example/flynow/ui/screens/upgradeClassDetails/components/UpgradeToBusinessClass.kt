package com.example.flynow.ui.screens.upgradeClassDetails.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.UpgradeClass
import com.example.flynow.navigation.UpgradeClassDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.upgradeClassDetails.UpgradeClassDetailsViewModel
import com.example.flynow.utils.Constants

//component that shows the upgrade to business route
//and takes the price, info from the query and the variable that will be stored the
//selected values from the user
@Composable
fun UpgradeToBusinessClass(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    upgradeClassDetailsViewModel: UpgradeClassDetailsViewModel
){
    val numOfFlights = sharedViewModel.upgradeToBusinessInfo.size//outbound inbound

    if(upgradeClassDetailsViewModel.selectedUpgradeBusiness.size == 0 && sharedViewModel.upgradeToBusinessInfo.size == 1) {
        upgradeClassDetailsViewModel.selectedUpgradeBusiness.add(false)
    }
    else if(upgradeClassDetailsViewModel.selectedUpgradeBusiness.size == 0 && sharedViewModel.upgradeToBusinessInfo.size == 2) {
        upgradeClassDetailsViewModel.selectedUpgradeBusiness.add(false)
        upgradeClassDetailsViewModel.selectedUpgradeBusiness.add(false)
    }

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
                    upgradeClassDetailsViewModel.initializeVariables()
                    sharedViewModel.selectedIndex = 3
                    navController.navigate(UpgradeClass.route) {
                        popUpTo(UpgradeClassDetails.route)
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
                    text = "Upgrade to Business Class",
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
                    Icons.Outlined.KeyboardDoubleArrowUp,
                    contentDescription = "UpgradeClass",
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
            Image(
                painter = painterResource(id = R.drawable.upgradeclass),
                contentDescription = "upgradeClass",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 3f)
                    .padding(start = 10.dp, top = 5.dp, end = 10.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 65.dp)
            ) {
                items(numOfFlights) { index ->
                    BusinessCheckBox(
                        sharedViewModel = sharedViewModel,
                        upgradeClassDetailsViewModel = upgradeClassDetailsViewModel,
                        index = index
                    )
                    if (index == numOfFlights - 1) {
                        BusinessInfo()
                    }
                }
            }
        }
    }
}