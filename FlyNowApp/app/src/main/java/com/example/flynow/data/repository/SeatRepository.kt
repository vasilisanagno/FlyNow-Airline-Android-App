package com.example.flynow.data.repository

import android.util.Log
import com.example.flynow.data.network.FlyNowApi
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

//class that gets the airplane capacity and the booked seats
//in the airplane of a flight
class SeatRepository @Inject constructor(
    private val flyNowApi: FlyNowApi
) {
    suspend fun getAirplaneCapacity(
        airplaneModel: String
    ): Int {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        val responseApi: JSONArray
        var capacity = 0

        jsonObject.put("airplaneModel", airplaneModel)
        jsonArray.put(jsonObject)

        try {
            responseApi = flyNowApi.getCapacity(jsonArray)
            capacity = responseApi.getJSONObject(0).getInt("capacity")
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return capacity
    }

    suspend fun getAirplaneBookedSeats(
        flightId: String
    ): MutableList<String> {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        val responseApi: JSONArray
        val bookedSeats = mutableListOf<String>()

        jsonObject.put("flightId", flightId)
        jsonArray.put(jsonObject)

        try {
            responseApi = flyNowApi.getBookedSeats(jsonArray)
            for (i in 0 until responseApi.length()) {
                bookedSeats.add(responseApi.getJSONObject(i).getString("seatnumber"))
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return bookedSeats
    }
}