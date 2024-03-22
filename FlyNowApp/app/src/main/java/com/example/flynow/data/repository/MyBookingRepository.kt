package com.example.flynow.data.repository

import android.util.Log
import com.example.flynow.data.model.BookingResponse
import com.example.flynow.data.network.FlyNowApi
import com.example.flynow.data.repository.ParsingRepository.Companion.parseBaggageAndSeatPerPassengerJson
import com.example.flynow.data.repository.ParsingRepository.Companion.parseCarMyBookingJson
import com.example.flynow.data.repository.ParsingRepository.Companion.parseFlightsJson
import com.example.flynow.data.repository.ParsingRepository.Companion.parsePassengersJson
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

//class that gets data for a booking and delete a booking from the database
class MyBookingRepository @Inject constructor(
    private val flyNowApi: FlyNowApi
) {
    suspend fun getBookingData(bookingId: String): BookingResponse {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        val responseApi: JSONArray
        val bookingResponse = BookingResponse()

        jsonObject.put("bookingid", bookingId)
        jsonArray.put(jsonObject)
        try {
            responseApi = flyNowApi.getBookingDetails(jsonArray)
            bookingResponse.oneWay = responseApi.getJSONObject(0).getBoolean("oneway")
            bookingResponse.outboundDirect = responseApi.getJSONObject(1).getBoolean("outbounddirect")
            bookingResponse.inboundDirect = responseApi.getJSONObject(2).getBoolean("inbounddirect")
            val bookingFlights  = parseFlightsJson(responseApi.getJSONObject(3).getJSONArray("flights").toString())
            if(bookingFlights != null){
                bookingResponse.flightsMyBooking = bookingFlights
            }
            //baggage
            val bookingBaggageSeat = parseBaggageAndSeatPerPassengerJson(responseApi.getJSONObject(4).getJSONArray("baggagePerPassenger").toString())
            if(bookingBaggageSeat != null){
                bookingResponse.baggageAndSeatMyBooking = bookingBaggageSeat
            }

            bookingResponse.numOfPassengers = responseApi.getJSONObject(5).getInt("numofpassengers")
            //passengers
            val bookingPassengers = parsePassengersJson(responseApi.getJSONObject(7).getJSONArray("passengers").toString())
            if(bookingPassengers != null){
                bookingResponse.passengersMyBooking = bookingPassengers
            }

            //pet
            bookingResponse.petSizeMyBooking = responseApi.getJSONObject(8).getString("petsize")

            //wifi
            bookingResponse.wifiOnBoard = responseApi.getJSONObject(9).getInt("wifionboard")

            //cars
            val cars = parseCarMyBookingJson(responseApi.getJSONObject(10).getJSONArray("cars").toString())
            if(cars != null) {
                var carPrice = 0.0
                cars.forEach { car ->
                    carPrice += car!!.price
                }
                bookingResponse.carsMyBooking = cars
                bookingResponse.rentingTotalPrice = carPrice
            }
            bookingResponse.totalPriceMyBooking = responseApi.getJSONObject(11).getDouble("bookingPrice")
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return bookingResponse
    }

    suspend fun deleteBookingData(bookingId: String) {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        jsonObject.put("bookingId", bookingId)
        jsonArray.put(jsonObject)
        try {
            flyNowApi.deleteBooking(jsonArray)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }
}