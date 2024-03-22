package com.example.flynow.ui.screens.upgradeClass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.MoreRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that retrieves the class of the flights in the booking
@HiltViewModel
class UpgradeClassViewModel @Inject constructor(
    private val repository: MoreRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {

    fun getClassFromApi() {
        viewModelScope.launch {
            sharedViewModel.upgradeToBusinessInfo.clear()
            sharedViewModel.upgradeToBusinessInfo = repository.getClass(sharedViewModel.textBookingId)
        }
    }
}