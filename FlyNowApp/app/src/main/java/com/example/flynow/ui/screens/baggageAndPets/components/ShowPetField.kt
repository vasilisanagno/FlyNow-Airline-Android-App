package com.example.flynow.ui.screens.baggageAndPets.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel

//component that shows the pets with the radio options
@Composable
fun ShowPetField(
    state: String,
    baggageAndPetsViewModel: BaggageAndPetsViewModel,
    sharedViewModel: SharedViewModel
) {
    val option: MutableState<String> = remember {
        mutableStateOf("")
    }

    if(state != "PetsFromMore"){
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 3.dp,
            color = Color(0xFF023E8A)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp)
        ) {
            Text(
                text = "Pets",
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                color = Color(0xFF023E8A),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.Filled.Pets,
                contentDescription = "Pets",
                modifier = Modifier.padding(
                    start = 5.dp,
                    end = 45.dp,
                    top = 12.dp
                ),
                tint = Color(0xFF023E8A)
            )
        }
    }
    Column(
        Modifier
            .fillMaxHeight()
            .padding(bottom = 100.dp)) {
        Image(
            painter = painterResource(id = R.drawable.pet),
            contentDescription = "pet",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.7f)
                .padding(start = 10.dp, end = 10.dp)
        )
        if(state != "PetsFromMore") {
            Text(
                text = "Are you travelling with pet?",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 10.dp),
                color = Color(0xFF023E8A),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                fontWeight = FontWeight.Bold
            )
            option.value = radioButtonsPetYesNo(
                baggageAndPetsViewModel = baggageAndPetsViewModel
            )
        }
        else {//state == "PetsFromMore"
            Text(
                text = "Travel with Pet",
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 10.dp),
                color = Color(0xFF023E8A),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                fontWeight = FontWeight.Bold
            )
            option.value = "Yes"
        }
        if((state=="PetsFromMore" && sharedViewModel.petSize != "Large")
            || state=="Baggage&Pets") {
            if (option.value == "Yes") {
                Text(
                    text = "Pet Size",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 5.dp, start = 10.dp),
                    color = Color(0xFF023E8A),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    fontWeight = FontWeight.Bold
                )
                radioButtonsPetSize(
                    onChange = { selectedOption ->
                        val endString = selectedOption.length
                        sharedViewModel.tempPetPrice =
                            selectedOption.substring(endString - 3, endString - 1).toInt()
                    },
                    baggageAndPetsViewModel = baggageAndPetsViewModel,
                    sharedViewModel = sharedViewModel
                )
                when(sharedViewModel.tempPetPrice) {
                    35 -> {
                        sharedViewModel.selectedPetSize = "Small"
                    }
                    50 -> {
                        sharedViewModel.selectedPetSize = "Medium"
                    }
                    90 -> {
                        sharedViewModel.selectedPetSize = "Large"
                    }
                    15 -> {
                        sharedViewModel.selectedPetSize = "Medium"
                    }
                    55 -> {
                        sharedViewModel.selectedPetSize = "Large"
                    }
                    40 -> {
                        sharedViewModel.selectedPetSize = "Large"
                    }
                }
            }
            else {
                sharedViewModel.tempPetPrice = 0
                baggageAndPetsViewModel.selectedOptionForYes = ""
            }
        }
        else {
            Column {
                Text(
                    text = "Pet Size",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 5.dp, start = 10.dp),
                    color = Color(0xFF023E8A),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "You have picked already the largest size of pet(>25kg)!",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp),
                    color = Color(0xFF023E8A),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )
                )
            }
        }
    }
}