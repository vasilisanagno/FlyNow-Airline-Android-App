package com.example.flynow.data.network

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.example.flynow.utils.Constants
import kotlinx.coroutines.delay
import org.json.JSONArray
import javax.inject.Inject

//implementation of the functions of interface FlyNowApi
class FlyNowApiImpl @Inject constructor(
    private val requestQueue: RequestQueue
) : FlyNowApi {

    private suspend fun getRequest(url: String): JSONArray {
        var responseApi = JSONArray()

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                responseApi = response
            },
            { error ->
                Log.d("ErrorAPI", error.toString())
            }
        )
        requestQueue.add(request)
        delay(1000)

        return responseApi
    }

    private suspend fun postRequest(url: String, data: JSONArray, delay: Long): JSONArray {
        var responseApi = JSONArray()

        val request = JsonArrayRequest(
            Request.Method.POST, url, data,
            { response ->
                responseApi = response
            },
            { error ->
                Log.d("ErrorAPI", error.toString())
            }
        )
        requestQueue.add(request)
        delay(delay)

        return responseApi
    }

    override suspend fun getAirports(): JSONArray {
        val url = Constants.BASE_URL + "flynow/airports"

        return getRequest(url)
    }

    override suspend fun getFlights(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/flights"

        return postRequest(url,data,3000)
    }

    override suspend fun getCapacity(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/airplane-capacity"

        return postRequest(url,data,500)
    }

    override suspend fun getBookedSeats(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/seats"

        return postRequest(url,data,500)
    }

    override suspend fun insertBooking(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/new-booking"

        return postRequest(url,data,4000)
    }

    override suspend fun checkBooking(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/check-booking"

        return postRequest(url,data,1000)
    }

    override suspend fun getWifiOnBoard(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/wifi-on-board"

        return postRequest(url,data,2000)
    }

    override suspend fun updateWifiOnBoard(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/update-wifi"

        return postRequest(url,data,2000)
    }

    override suspend fun getClassOfFlights(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/upgrade-to-business"

        return postRequest(url,data,2000)
    }

    override suspend fun updateToBusinessClass(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/update-business"

        return postRequest(url,data,2000)
    }

    override suspend fun getBaggage(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/baggage-from-more"

        return postRequest(url,data,2000)
    }

    override suspend fun updateBaggage(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/update-baggage"

        return postRequest(url,data,2000)
    }

    override suspend fun getPets(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/pets-from-more"

        return postRequest(url,data,2000)
    }

    override suspend fun updatePets(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/update-pets"

        return postRequest(url,data,2000)
    }

    override suspend fun checkCarCredentials(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/car-booking-exists"

        return postRequest(url,data,1000)
    }

    override suspend fun getAvailableCars(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/cars"

        return postRequest(url,data,3000)
    }

    override suspend fun rentCar(data: JSONArray): JSONArray {
        val url = Constants.BASE_URL+"flynow/renting-car"

        return postRequest(url,data,3000)
    }

    override suspend fun getBookingDetails(data: JSONArray): JSONArray{
        val url =  Constants.BASE_URL+"flynow/booking-details"

        return postRequest(url,data,5000)
    }

    override suspend fun deleteBooking(data: JSONArray): JSONArray{
        val url =  Constants.BASE_URL+"flynow/delete-booking"

        return postRequest(url,data,3000)
    }

    override suspend fun getCheckInData(data: JSONArray): JSONArray{
        val url =  Constants.BASE_URL+"flynow/checkin-details"

        return postRequest(url,data,2000)
    }

    override suspend fun updateCheckIn(data: JSONArray): JSONArray{
        val url =  Constants.BASE_URL+"flynow/update-checkin"

        return postRequest(url,data,3000)
    }
}