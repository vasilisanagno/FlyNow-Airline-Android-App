package com.example.flynow.model

import android.graphics.Bitmap
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState

//data class that stores info about cars for the rent a car
data class CarDetails(
    val carImage: MutableState<Bitmap>,
    val company: MutableState<String>,
    val model: MutableState<String>,
    val price: MutableDoubleState,
    val carId: MutableIntState
)