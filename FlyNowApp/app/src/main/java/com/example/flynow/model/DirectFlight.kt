package com.example.flynow.model

import com.google.gson.annotations.SerializedName

//data class for flights that are direct to reach in a destination
data class DirectFlight(
    @SerializedName("flightid") val flightId: String,
    @SerializedName("flightdate") val flightDate: String,
    @SerializedName("departuretime") val departureTime: String,
    @SerializedName("arrivaltime") val arrivalTime: String,
    @SerializedName("economyprice") val economyPrice: Double,
    @SerializedName("flexprice") val flexPrice: Double,
    @SerializedName("businessprice") val businessPrice: Double,
    @SerializedName("departureairport") val departureAirport: String,
    @SerializedName("arrivalairport") val arrivalAirport: String,
    @SerializedName("departurecity") val departureCity: String,
    @SerializedName("arrivalcity") val arrivalCity: String,
    @SerializedName("airplanemodel") val airplaneModel: String,
    @SerializedName("flightduration") val flightDuration: String
)