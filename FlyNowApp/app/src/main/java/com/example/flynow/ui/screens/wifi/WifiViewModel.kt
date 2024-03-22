package com.example.flynow.ui.screens.wifi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.MoreRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that retrieves the wifi type of the booking
@HiltViewModel
class WifiViewModel @Inject constructor(
    private val repository: MoreRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {

    fun getWifiFromApi() {
        viewModelScope.launch {
            sharedViewModel.wifiInfo = repository.getWifi(sharedViewModel.textBookingId)
        }
    }
}