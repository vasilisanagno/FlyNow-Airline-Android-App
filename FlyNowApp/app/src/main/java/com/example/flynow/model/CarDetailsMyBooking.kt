package com.example.flynow.model

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

//data class that stores info about cars for the my booking
data class CarDetailsMyBooking(
    @SerializedName("carimage") var carImage: Bitmap,
    @SerializedName("company") val company: String,
    @SerializedName("model") val model: String,
    @SerializedName("price") val price: Double,
    @SerializedName("location") val location: String,
    @SerializedName("pickup") val pickUpDateTime: String,
    @SerializedName("return") val returnDateTime: String
)