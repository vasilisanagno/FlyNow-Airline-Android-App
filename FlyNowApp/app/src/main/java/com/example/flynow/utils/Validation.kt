package com.example.flynow.utils

//class that contains the function to check if the email is valid
//with the correct type according the regex
class Validation private constructor() {
    companion object {
        //function that checks if the email is valid
        fun isValidEmail(email: String): Boolean {
            val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
            return email.matches(emailRegex)
        }
    }
}