package com.example.flynow.model

import com.google.gson.annotations.SerializedName

//data class for the info about the baggage and seats per passenger
data class BaggageAndSeatPerPassenger(
    @SerializedName("gender") val gender: String,
    @SerializedName("firstname") val firstname: String,
    @SerializedName("lastname") val lastname: String,
    @SerializedName("flightid") val flightid: String,
    @SerializedName("reservationid") val reservationid: String,
    @SerializedName("baggage23kg") val baggage23kg: Int,
    @SerializedName("baggage32kg") val baggage32kg: Int,
    @SerializedName("seatnumber") val seatnumber: String,
    @SerializedName("departurecity") val departurecity: String,
    @SerializedName("arrivalcity") val arrivalcity: String,
    @SerializedName("checkin") val checkin: Boolean
)