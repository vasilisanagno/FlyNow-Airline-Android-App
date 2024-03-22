package com.example.flynow.ui.screens.more.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.More
import com.example.flynow.ui.SharedViewModel

//component that shows the card option in the top of "More" Screen
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun CardOption(
    navController: NavController,
    interactionSource: MutableInteractionSource,
    highlightIndication: Indication,
    title: String,
    icon: ImageVector,
    route: String,
    sharedViewModel: SharedViewModel
) {
    Card(
        modifier = Modifier
            .padding(top = 20.dp)
            .width(130.dp)
            .height(130.dp)
            .clickable(interactionSource = interactionSource,
                indication = highlightIndication,
                enabled = true, onClickLabel = null, onClick = {
                    if(title == "Change your Booking"){
                        sharedViewModel.selectedIndex= 2
                    }
                    navController.navigate(route) {
                        popUpTo(More.route)
                        launchSingleTop = true
                    }
                }),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 20.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF00B4D8),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = title,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 20.dp),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                textAlign = TextAlign.Center,
                color = Color(0xFF023E8A)
            )
        }
    }
}
