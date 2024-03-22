package com.example.flynow.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R

//component that makes the style of the button that is used throughout the app
@Composable
fun FlyNowButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    //Button in the app that has the same design
    ElevatedButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp
        ),
        enabled = enabled
    ) {
        Text(text = text,
            fontSize = 18.sp,
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