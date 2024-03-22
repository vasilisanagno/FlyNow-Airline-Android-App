package com.example.flynow.ui.screens.cars

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.CarRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that communicates with the server to complete a car rental
//keeps the state of the screen variables and initializes them
@HiltViewModel
class CarsViewModel @Inject constructor(
    private val repository: CarRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    //these two variables are for the failure of the searching, if there are not flights
    var noResults by mutableIntStateOf(1)
    var totalPriceForCar by mutableDoubleStateOf(0.0)

    //variables that shows the dialog for the completion of the renting of car
    var showDialog by mutableStateOf(false)

    var showDialogConfirm by mutableStateOf(false)
    var insertNewBookingOfCar by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.O)
    fun rentACar() {
        viewModelScope.launch {
            repository.insertCarRental(
                sharedViewModel.listOfButtonsCars,
                sharedViewModel.listOfCars,
                sharedViewModel.pickUpDateCar,
                sharedViewModel.pickUpHour,
                sharedViewModel.pickUpMins,
                sharedViewModel.returnDateCar,
                sharedViewModel.returnHour,
                sharedViewModel.returnMins,
                sharedViewModel.textBookingId,
                totalPriceForCar*sharedViewModel.daysDifference
            )
        }
    }

    fun finishInsertCar(): Boolean {
        showDialog = true

        return false
    }

    fun initializeVariables() {
        noResults = 1
        totalPriceForCar = 0.0
        showDialog = false
        showDialogConfirm = false
        insertNewBookingOfCar = false
        sharedViewModel.buttonClickedCredentials = false
        sharedViewModel.rentCar = false
        sharedViewModel.textBookingId = ""
        sharedViewModel.locationToRentCar = ""
        sharedViewModel.listOfCars.clear()
        sharedViewModel.pickUpDateCar = ""
        sharedViewModel.pickUpHour = "10"
        sharedViewModel.pickUpMins = "30"
        sharedViewModel.returnDateCar = ""
        sharedViewModel.returnHour = "10"
        sharedViewModel.returnMins = "30"
        sharedViewModel.daysDifference = 1
    }
}