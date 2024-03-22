package com.example.flynow.data.repository

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.flynow.data.model.CheckInResponse
import com.example.flynow.data.network.FlyNowApi
import com.example.flynow.data.repository.ParsingRepository.Companion.parseBaggageAndSeatPerPassengerJson
import com.example.flynow.data.repository.ParsingRepository.Companion.parseFlightsJson
import com.example.flynow.data.repository.ParsingRepository.Companion.parsePassengersJson
import com.example.flynow.model.BasicFlight
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

//class that contains the communication about retrieve and
//update of check-in data
class CheckInRepository @Inject constructor(
    private val flyNowApi: FlyNowApi
) {
    suspend fun getCheckInData(bookingId: String): CheckInResponse {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        val responseApi: JSONArray
        val checkInResponse = CheckInResponse()

        jsonObject.put("bookingid", bookingId)
        jsonArray.put(jsonObject)
        try {
            responseApi = flyNowApi.getCheckInData(jsonArray)
            if (responseApi.getJSONObject(0).getInt("numofflightstocheckin") == 0) {
                checkInResponse.checkInOpen = false
            } else {
                Log.d("response", "flights for checkin")
                checkInResponse.checkInOpen = true
            }

            //flights
            checkInResponse.directFlight = responseApi.getJSONObject(1).getBoolean("direct")
            val checkInFlights =
                parseFlightsJson(responseApi.getJSONObject(2).getJSONArray("flights").toString())

            if (checkInFlights != null) {
                checkInResponse.flightsCheckIn = checkInFlights
            }

            //baggage
            val bookingBaggageSeat = parseBaggageAndSeatPerPassengerJson(
                responseApi.getJSONObject(3).getJSONArray("baggagePerPassenger").toString()
            )
            if (bookingBaggageSeat != null) {
                checkInResponse.baggageAndSeatCheckIn = bookingBaggageSeat
            }

            checkInResponse.numOfPassengersCheckIn =
                responseApi.getJSONObject(4).getInt("numofpassengers")

            //passengers
            val checkInPassengers = parsePassengersJson(
                responseApi.getJSONObject(6).getJSONArray("passengers").toString()
            )
            if (checkInPassengers != null) {
                checkInResponse.passengersCheckIn = checkInPassengers
            }
            //pet
            checkInResponse.petSizeCheckIn = responseApi.getJSONObject(7).getString("petsize")

            //wifi
            checkInResponse.wifiOnBoardCheckIn = responseApi.getJSONObject(8).getInt("wifionboard")

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return checkInResponse
    }

    suspend fun updateCheckInData(
        bookingId: String,
        flightsCheckIn: SnapshotStateList<BasicFlight?>
    ) {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("bookingid", bookingId)
        jsonObject.put("flightid1", flightsCheckIn[0]?.flightId)
        if(flightsCheckIn.size > 1){
            jsonObject.put("flightid2", flightsCheckIn[1]?.flightId)
            jsonObject.put("numofflights", 2)
        }
        else{
            jsonObject.put("flightid2", "")
            jsonObject.put("numofflights", 1)
        }
        jsonArray.put(jsonObject)
        try {
            flyNowApi.updateCheckIn(jsonArray)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}