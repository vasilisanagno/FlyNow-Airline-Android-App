package com.example.flynow.data.repository

import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.flynow.data.model.BaggagePerPassengerResponse
import com.example.flynow.data.network.FlyNowApi
import com.example.flynow.model.PassengerInfo
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

//class that has the communication with the server about 4 more screen options
//"Upgrade to Business Class", "Extra baggage", "Travelling with pets",
//"Upgrade wifi on board" and there is two functionalities for each option
//retrieve and update data for an option
class MoreRepository @Inject constructor(
    private val flyNowApi: FlyNowApi
) {
    suspend fun getWifi(bookingId: String): Int {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("bookingid", bookingId)
        jsonArray.put(jsonObject)
        var responseApi = JSONArray()

        try {
            responseApi = flyNowApi.getWifiOnBoard(jsonArray)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return responseApi.getJSONObject(0).getInt("wifiOnBoard")
    }

    suspend fun updateWifi(
        bookingId: String,
        selectedWifi: Int,
        wifiPrice: Double
    ) {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("bookingid", bookingId)
        jsonObject.put("wifiOnBoard", selectedWifi)
        jsonObject.put("price", wifiPrice)
        jsonArray.put(jsonObject)

        try {
            flyNowApi.updateWifiOnBoard(jsonArray)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }

    suspend fun getClass(bookingId: String): MutableList<String> {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        val upgradeToBusinessInfo: MutableList<String> = mutableListOf()
        jsonObject.put("bookingId", bookingId)
        jsonArray.put(jsonObject)
        val responseApi: JSONArray

        try {
            responseApi = flyNowApi.getClassOfFlights(jsonArray)
            if(responseApi.getJSONObject(0).getBoolean("oneWay")) {
                upgradeToBusinessInfo.add("")
                upgradeToBusinessInfo[0] = responseApi.getJSONObject(0)
                    .getJSONObject("outbound")
                    .getString("classType")
            }
            else {
                upgradeToBusinessInfo.add("")
                upgradeToBusinessInfo.add("")
                upgradeToBusinessInfo[0] = responseApi.getJSONObject(0)
                    .getJSONObject("outbound")
                    .getString("classType")
                upgradeToBusinessInfo[1] = responseApi.getJSONObject(0)
                    .getJSONObject("inbound")
                    .getString("classType")
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return upgradeToBusinessInfo
    }

    suspend fun updateClass(
        bookingId: String,
        selectedUpgradeBusiness: MutableList<Boolean>,
        upgradeBusinessPrice: Double
    ) {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("bookingId", bookingId)
        if(selectedUpgradeBusiness.size == 1) {
            jsonObject.put("outbound", selectedUpgradeBusiness[0])
            jsonObject.put("inbound", false)
        }
        else {
            jsonObject.put("outbound", selectedUpgradeBusiness[0])
            jsonObject.put("inbound", selectedUpgradeBusiness[1])
        }
        jsonObject.put("price", upgradeBusinessPrice)
        jsonArray.put(jsonObject)

        try {
            flyNowApi.updateToBusinessClass(jsonArray)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }

    suspend fun getBaggagePerPassenger(bookingId: String): BaggagePerPassengerResponse {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("bookingId", bookingId)
        jsonArray.put(jsonObject)
        val responseApi: JSONArray
        val baggagePerPassenger = BaggagePerPassengerResponse()

        try {
            responseApi = flyNowApi.getBaggage(jsonArray)
            baggagePerPassenger.passengersCounter = responseApi.length() - 1
            baggagePerPassenger.oneWayInBaggage = responseApi.getJSONObject(0).getBoolean("oneWay")
            for(i in 1 until baggagePerPassenger.passengersCounter + 1) {
                baggagePerPassenger.passengers.add(
                    PassengerInfo(
                        mutableStateOf(""), mutableStateOf(""),
                        mutableStateOf(""), mutableStateOf(""),
                        mutableStateOf(""), mutableStateOf("")
                    )
                )
                baggagePerPassenger.passengers[i-1].firstname.value = responseApi.getJSONObject(i).getString("firstname")
                baggagePerPassenger.passengers[i-1].lastname.value = responseApi.getJSONObject(i).getString("lastname")
                baggagePerPassenger.passengers[i-1].gender.value = responseApi.getJSONObject(i).getString("gender")
                baggagePerPassenger.passengers[i-1].birthdate.value = responseApi.getJSONObject(i).getString("birthdate")
                baggagePerPassenger.passengers[i-1].email.value = responseApi.getJSONObject(i).getString("email")
                baggagePerPassenger.passengers[i-1].phonenumber.value = responseApi.getJSONObject(i).getString("phonenumber")
            }
            for(i in 0 until baggagePerPassenger.passengersCounter) {
                baggagePerPassenger.limitBaggageFromMore.add(0)
                baggagePerPassenger.limitBaggageFromMore[i] = responseApi.getJSONObject(i+1).getInt("baggageOutbound")
            }
            if(!baggagePerPassenger.oneWayInBaggage) {
                for(i in 0 until baggagePerPassenger.passengersCounter) {
                    baggagePerPassenger.limitBaggageFromMore.add(0)
                    baggagePerPassenger.limitBaggageFromMore[i+baggagePerPassenger.passengersCounter] = responseApi.getJSONObject(i+1).getInt("baggageInbound")
                }
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return baggagePerPassenger
    }

    suspend fun updateBaggagePerPassenger(
        bookingId: String,
        numOfPassengers: Int,
        oneWay: Boolean,
        passengersInfo: MutableList<PassengerInfo>,
        baggagePrice: Double,
        selectedBaggage: SnapshotStateList<MutableList<MutableIntState>>
    ) {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("bookingId", bookingId)
        val baggageJSONArray = JSONArray()
        if (oneWay) {
            for (i in 0 until numOfPassengers) {
                val baggageJSONObject = JSONObject()
                val baggageJSONObjectOutbound = JSONObject()
                baggageJSONObject.put("email", passengersInfo[i].email.value)
                baggageJSONObjectOutbound.put("baggage23kg", selectedBaggage[i][0].intValue)
                baggageJSONObjectOutbound.put("baggage32kg", selectedBaggage[i][1].intValue)
                baggageJSONObject.put("outbound", baggageJSONObjectOutbound)
                baggageJSONArray.put(baggageJSONObject)
            }
            jsonObject.put("oneWay", true)
            jsonObject.put("baggage", baggageJSONArray)
        }
        else {
            for (i in 0 until numOfPassengers) {
                val baggageJSONObject = JSONObject()
                val baggageJSONObjectOutbound = JSONObject()
                val baggageJSONObjectInbound = JSONObject()
                baggageJSONObject.put("email", passengersInfo[i].email.value)
                baggageJSONObjectOutbound.put("baggage23kg", selectedBaggage[i][0].intValue)
                baggageJSONObjectOutbound.put("baggage32kg", selectedBaggage[i][1].intValue)
                baggageJSONObject.put("outbound", baggageJSONObjectOutbound)
                baggageJSONObjectInbound.put(
                    "baggage23kg",
                    selectedBaggage[i + numOfPassengers][0].intValue
                )
                baggageJSONObjectInbound.put(
                    "baggage32kg",
                    selectedBaggage[i + numOfPassengers][1].intValue
                )
                baggageJSONObject.put("inbound", baggageJSONObjectInbound)
                baggageJSONArray.put(baggageJSONObject)
            }
            jsonObject.put("oneWay", false)
            jsonObject.put("baggage", baggageJSONArray)
        }
        jsonObject.put("price", baggagePrice)
        jsonArray.put(jsonObject)

        try {
            flyNowApi.updateBaggage(jsonArray)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }

    suspend fun getPetsForTheReservation(bookingId: String): String {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("bookingid", bookingId)
        jsonArray.put(jsonObject)
        var responseApi = JSONArray()

        try {
            responseApi = flyNowApi.getPets(jsonArray)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return responseApi.getJSONObject(0).getString("petSize")
    }

    suspend fun updatePetsInTheReservation(
        bookingId: String,
        selectedPetSize: String,
        petsPrice: Double
    ) {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("bookingid", bookingId)
        jsonObject.put("petSize", selectedPetSize)
        jsonObject.put("price", petsPrice)
        jsonArray.put(jsonObject)

        try {
            flyNowApi.updatePets(jsonArray)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }
}