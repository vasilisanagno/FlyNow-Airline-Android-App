package com.example.flynow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that is useful about the splash screen
//and keeps the variable bottom bar state which is useful about
//the bottom navigation
@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {
    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()
    //bottomBar state true or false accordingly if one page has bottom bar or not
    var bottomBarState by mutableStateOf(true)

    init {
        viewModelScope.launch {
            // run background task here
            delay(800)
            _loading.value = false
        }
    }
}