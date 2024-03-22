package com.example.flynow.ui.screens.upgradeClassDetails

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.MoreRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that completes the update of the class selection and
// keeps/initializes the state of the variables
@SuppressLint("MutableCollectionMutableState")
@HiltViewModel
class UpgradeClassDetailsViewModel @Inject constructor(
    private val repository: MoreRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    var buttonClicked by mutableStateOf(false)
    var upgradeBusinessPrice by mutableDoubleStateOf(0.0)
    //list to store the selected values for the outbound and inbound for business option
    var selectedUpgradeBusiness by mutableStateOf(mutableListOf<Boolean>())

    fun updateClass() {
        viewModelScope.launch {
            repository.updateClass(
                bookingId = sharedViewModel.textBookingId,
                selectedUpgradeBusiness = selectedUpgradeBusiness,
                upgradeBusinessPrice = upgradeBusinessPrice
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
        upgradeBusinessPrice = 0.0
        selectedUpgradeBusiness.clear()
        sharedViewModel.showDialog = false
        buttonClicked = false
        sharedViewModel.updateBusiness = false
        sharedViewModel.upgradeToBusinessMore = false
        sharedViewModel.upgradeToBusinessInfo.clear()
    }
}