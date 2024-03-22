package com.example.flynow.ui.screens.petsFromMore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.MoreRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that retrieves the pet size
@HiltViewModel
class PetsViewModel @Inject constructor(
    private val repository: MoreRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {

    fun getPets() {
        viewModelScope.launch {
            sharedViewModel.petSize = repository.getPetsForTheReservation(sharedViewModel.textBookingId)
        }
    }
}