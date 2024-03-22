package com.example.flynow.ui.screens.passengers.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowTextField
import com.example.flynow.utils.Constants

//component that creates the dropdown menu for the gender selection
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdownMenu(
    buttonClicked: Boolean,
    sharedViewModel: SharedViewModel,
    index: Int
) {
    if(sharedViewModel.passengers.size!=0) {
        val isExpanded = remember {
            mutableStateOf(false)
        }
        ExposedDropdownMenuBox(
            expanded = isExpanded.value,
            onExpandedChange = { newValue ->
                isExpanded.value = newValue
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
        ) {
            FlyNowTextField(
                text = sharedViewModel.passengers[index].gender.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = buildAnnotatedString {
                    append("Gender")
                    withStyle(Constants.superscript) {
                        append("*")
                    }
                }.toString(),
                readOnly = true,
                onTextChange = {},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                isError = (sharedViewModel.passengers[index].gender.value == "" && buttonClicked)
            )

            ExposedDropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = {
                    isExpanded.value = false
                },
                modifier = Modifier
                    .width(200.dp)
                    .background(color = Color.White)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Male",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            )
                        )
                    },
                    onClick = {
                        sharedViewModel.passengers[index].gender.value = "Male"
                        isExpanded.value = false
                    },
                    modifier = Modifier.background(color = Color.White)
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Female",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            )
                        )
                    },
                    onClick = {
                        sharedViewModel.passengers[index].gender.value = "Female"
                        isExpanded.value = false
                    },
                    modifier = Modifier.background(color = Color.White)
                )
            }
        }
    }
}