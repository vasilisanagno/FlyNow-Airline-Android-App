package com.example.flynow.data.repository

import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.example.flynow.data.model.CarCredentialsResponse
import com.example.flynow.data.network.FlyNowApi
import com.example.flynow.model.CarDetails
import com.example.flynow.utils.Converters
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

//class that checks the car credentials if there is no problem,
//retrieves the available cars and inserts the new car rental in the database
class CarRepository @Inject constructor(
    private val flyNowApi: FlyNowApi
) {
    suspend fun checkCarData(
        bookingId: String,
        locationToRentCar: String,
        pickUpDateCar: String,
        pickUpHour: String,
        pickUpMins: String,
        returnDateCar: String,
        returnHour: String,
        returnMins: String
    ): CarCredentialsResponse {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        val responseApi: JSONArray
        val response = CarCredentialsResponse()
        jsonObject.put("bookingId", bookingId)
        jsonObject.put("location", locationToRentCar)
        jsonObject.put("pickUpDate", pickUpDateCar)
        val pickUpHours = pickUpHour.toInt()
        jsonObject.put("pickUpHours", pickUpHours)
        val pickUpMinutes = pickUpMins.toInt()
        jsonObject.put("pickUpMinutes", pickUpMinutes)

        jsonObject.put("returnDate", returnDateCar)
        val returnHours = returnHour.toInt()
        jsonObject.put("returnHours", returnHours)
        val returnMinutes = returnMins.toInt()
        jsonObject.put("returnMinutes", returnMinutes)
        jsonArray.put(jsonObject)

        try {
            responseApi = flyNowApi.checkCarCredentials(jsonArray)
            response.bookingError = !responseApi.getJSONObject(0).getBoolean("success")
            response.airportError = !responseApi.getJSONObject(0).getBoolean("successAirport")
            response.rentingTimeError = !responseApi.getJSONObject(0).getBoolean("successTime")
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return response
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCars(
        locationToRentCar: String,
        pickUpDateCar: String,
        pickUpHour: String,
        pickUpMins: String,
        returnDateCar: String,
        returnHour: String,
        returnMins: String
    ): MutableList<CarDetails> {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        val responseApi: JSONArray
        val listOfCars: MutableList<CarDetails> = mutableListOf()
        jsonObject.put("location", locationToRentCar)

        //converts the date, hours and minutes strings to datetime object of pattern "dd/MM/yyyy HH:mm"
        //for the pick up and return
        val pickUpDateComponents = pickUpDateCar.split("/")
        val pickUpDay = pickUpDateComponents[0].toInt()
        val pickUpMonth = pickUpDateComponents[1].toInt()
        val pickUpYear = pickUpDateComponents[2].toInt()

        val pickUpHours = pickUpHour.toInt()
        val pickUpMinutes = pickUpMins.toInt()
        // Create a LocalDateTime object
        val pickUpDateTime = LocalDateTime.of(pickUpYear, pickUpMonth, pickUpDay, pickUpHours, pickUpMinutes)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val pickUpFormattedDateTime = pickUpDateTime.format(formatter)
        jsonObject.put("pickUp", pickUpFormattedDateTime)

        val returnDateComponents = returnDateCar.split("/")
        val returnDay = returnDateComponents[0].toInt()
        val returnMonth = returnDateComponents[1].toInt()
        val returnYear = returnDateComponents[2].toInt()

        val returnHours = returnHour.toInt()
        val returnMinutes = returnMins.toInt()
        // Create a LocalDateTime object
        val returnDateTime = LocalDateTime.of(returnYear, returnMonth, returnDay, returnHours, returnMinutes)
        val returnFormattedDateTime = returnDateTime.format(formatter)
        jsonObject.put("return", returnFormattedDateTime)
        jsonArray.put(jsonObject)

        try {
            responseApi = flyNowApi.getAvailableCars(jsonArray)
            for (i in 0 until responseApi.length()) {
                listOfCars.add(
                    CarDetails(
                        mutableStateOf(
                        Bitmap.createBitmap(
                            400,
                            400,
                            Bitmap.Config.ARGB_8888
                        )
                    ),
                        mutableStateOf(""),
                        mutableStateOf(""),
                        mutableDoubleStateOf(0.00),
                        mutableIntStateOf(0)

                    )
                )
                // Get the base64-encoded string from the response
                val base64ImageData: String = responseApi.getJSONObject(i).getString("carimage")

                // Decode the base64 string into a byte array
                val decodedBytes: ByteArray = Base64.decode(base64ImageData, Base64.DEFAULT)

                // Convert the byte array to a Bitmap
                listOfCars[i].carImage.value = Converters.byteArrayToBitmap(decodedBytes)
                //stores the rest of the other variables
                listOfCars[i].company.value = responseApi.getJSONObject(i).getString("company")
                listOfCars[i].model.value = responseApi.getJSONObject(i).getString("model")
                listOfCars[i].price.doubleValue = responseApi.getJSONObject(i).getDouble("price")
                listOfCars[i].carId.intValue = responseApi.getJSONObject(i).getInt("carid")
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return listOfCars
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertCarRental(
        listOfButtonsCars: MutableList<MutableState<Boolean>>,
        listOfCars: MutableList<CarDetails>,
        pickUpDateCar: String,
        pickUpHour: String,
        pickUpMins: String,
        returnDateCar: String,
        returnHour: String,
        returnMins: String,
        bookingId: String,
        totalPrice: Double
    ) {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        for(i in 0 until listOfButtonsCars.size) {
            if(listOfButtonsCars[i].value) {
                //converts the date, hours and minutes strings to datetime object of pattern "dd/MM/yyyy HH:mm"
                //for the pick up and return
                val pickUpDateComponents = pickUpDateCar.split("/")
                val pickUpDay = pickUpDateComponents[0].toInt()
                val pickUpMonth = pickUpDateComponents[1].toInt()
                val pickUpYear = pickUpDateComponents[2].toInt()

                val pickUpHours = pickUpHour.toInt()
                val pickUpMinutes = pickUpMins.toInt()
                // Create a LocalDateTime object
                val pickUpDateTime = LocalDateTime.of(pickUpYear, pickUpMonth, pickUpDay, pickUpHours, pickUpMinutes)
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                val pickUpFormattedDateTime = pickUpDateTime.format(formatter)
                jsonObject.put("pickUp", pickUpFormattedDateTime)

                val returnDateComponents = returnDateCar.split("/")
                val returnDay = returnDateComponents[0].toInt()
                val returnMonth = returnDateComponents[1].toInt()
                val returnYear = returnDateComponents[2].toInt()

                val returnHours = returnHour.toInt()
                val returnMinutes = returnMins.toInt()
                // Create a LocalDateTime object
                val returnDateTime = LocalDateTime.of(returnYear, returnMonth, returnDay, returnHours, returnMinutes)
                val returnFormattedDateTime = returnDateTime.format(formatter)
                jsonObject.put("return", returnFormattedDateTime)

                jsonObject.put("bookingId", bookingId)
                jsonObject.put("carId", listOfCars[i].carId.intValue)
                jsonObject.put("price", totalPrice)
                break
            }
        }
        jsonArray.put(jsonObject)

        try {
           flyNowApi.rentCar(jsonArray)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }

    }
}