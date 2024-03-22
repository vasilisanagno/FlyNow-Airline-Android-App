package com.example.flynow.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.sp

//constants that are used throughout the app
object Constants {
    const val BASE_URL = "http://100.106.205.30:5000/"

    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    //Superscript for the "*" that means mandatory field
    val superscript = SpanStyle(
        baselineShift = BaselineShift.Superscript,
        fontSize = 16.sp
    )
}