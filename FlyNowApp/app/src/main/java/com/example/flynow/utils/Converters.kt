package com.example.flynow.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

//class that contains functions which make different converts
class Converters private constructor() {
    companion object {
        //converts the milliseconds to date format that is shown below
        @SuppressLint("SimpleDateFormat")
        fun convertMillisToDate(millis: Long): String {
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            return formatter.format(Date(millis))
        }

        //function tha converts date to the milliseconds format below
        fun convertDateToMillis(dateString: String): Long {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = dateFormat.parse(dateString)
            return date?.time ?: 0L
        }

        //function that converts the byte array to bitmap
        fun byteArrayToBitmap(data: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(data, 0, data.size)
        }

        //this function helps to decode to hours and minutes the duration of the flights
        fun parseTime(input: String): Pair<Int, Int> {
            return when {
                input.length == 2 -> Pair(input[0].digitToInt(), 0)
                input.length == 3 -> Pair(input.substring(0, 2).toIntOrNull() ?: 0, 0)
                input.length == 4 -> Pair(0, input.substring(0, 2).toIntOrNull() ?: 0)
                input.length == 5 -> Pair(0, input.substring(0, 2).toIntOrNull() ?: 0)
                input.length == 7 -> Pair(
                    input[0].digitToInt(),
                    input.substring(3, 4).toIntOrNull() ?: 0
                )

                input.length == 8 && input[1] == 'h' -> Pair(
                    input[0].digitToInt(),
                    input.substring(3, 5).toIntOrNull() ?: 0
                )

                input.length == 8 && input[2] == 'h' -> Pair(
                    input.substring(0, 2).toIntOrNull() ?: 0,
                    input.substring(4, 5).toIntOrNull() ?: 0
                )

                input.length == 9 -> Pair(
                    input.substring(0, 2).toIntOrNull() ?: 0,
                    input.substring(4, 6).toIntOrNull() ?: 0
                )

                else -> Pair(0, 0)
            }
        }

        //converts the date to string format of EEEE d MMMM yyyy
        @RequiresApi(Build.VERSION_CODES.O)
        fun dateToString(dateInNums: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val outputFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ENGLISH)
            var date: LocalDate? = null

            try {
                // Parse the input date string to a LocalDate object
                date = LocalDate.parse(dateInNums, inputFormatter)
            } catch (e: Exception) {
                Log.d("Invalid date format", dateInNums)
            }
            // Format the LocalDate object to the desired output string format
            return date!!.format(outputFormatter)
        }
    }
}