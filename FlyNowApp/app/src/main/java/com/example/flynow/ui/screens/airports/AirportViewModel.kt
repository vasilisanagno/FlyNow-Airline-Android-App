package com.example.flynow.ui.screens.airports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.AirportRepository
import com.example.flynow.model.Airport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class for searching inside the list of airports when typing letters in the text field
//and gets them from the server
@OptIn(FlowPreview::class)
@HiltViewModel
class AirportViewModel @Inject constructor(
    private val repository: AirportRepository
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _airports = MutableStateFlow<List<Airport>>(emptyList())
    val airports = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_airports) { text, airports ->
            if (text.isBlank()) {
                airports
            } else {
                delay(2000L)
                airports.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        // Initialize airports on viewModel creation
        fetchDataFromApi()
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    //function that runs in the initialization of the airports list that the api returns with the get method
    private fun fetchDataFromApi() {
        viewModelScope.launch {
            _airports.value = repository.getAllAirports()
        }
    }
}
