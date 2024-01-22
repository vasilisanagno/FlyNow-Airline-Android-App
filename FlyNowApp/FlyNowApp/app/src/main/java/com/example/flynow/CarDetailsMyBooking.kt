package com.example.flynow

import android.graphics.Bitmap
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState

//data class that stores info about cars for the my booking
data class CarDetailsMyBooking(
    val carImage: MutableState<Bitmap>,
    val company: MutableState<String>,
    val model: MutableState<String>,
    val price: MutableDoubleState,
    val location: MutableState<String>,
    val pickUpDateTime: MutableState<String>,
    val returnDateTime: MutableState<String>
)