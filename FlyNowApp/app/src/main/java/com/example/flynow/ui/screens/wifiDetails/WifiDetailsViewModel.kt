package com.example.flynow.ui.screens.wifiDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.MoreRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that completes the update of the wifi selection and
// keeps/initializes the state of the variables
@HiltViewModel
class WifiDetailsViewModel @Inject constructor(
    private val repository: MoreRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    val radioOptions = listOf(
        "Web browsing & Social Media, \nup to 1.5Mbps - 6€",
        "Audio/Video streaming, High speed Web Browsing & Social Media, \nup to 15Mbps - 12€")
    var selectedOption by mutableStateOf("")

    var wifiPrice by mutableDoubleStateOf(0.0)
    //wifi option that the user selected
    var selectedWifi by mutableIntStateOf(0)
    var buttonClicked by mutableStateOf(false)

    fun updateWifi() {
        viewModelScope.launch {
            repository.updateWifi(
                bookingId = sharedViewModel.textBookingId,
                selectedWifi = selectedWifi,
                wifiPrice = wifiPrice
            )
        }
    }

    fun finishUpdate(): Boolean {
        buttonClicked = true
        sharedViewModel.showDialog = true

        return false
    }

    fun initializeVariables() {
        sharedViewModel.selectedIndex = 0
        sharedViewModel.textLastname = ""
        sharedViewModel.textBookingId = ""
        sharedViewModel.buttonClickedCredentials = false
        sharedViewModel.showProgressBar = false
        wifiPrice = 0.0
        selectedWifi = 0
        sharedViewModel.showDialog = false
        buttonClicked = false
        sharedViewModel.updateWifi = false
        selectedOption = ""
        sharedViewModel.wifiMore = false
        sharedViewModel.wifiInfo = -1
    }
}