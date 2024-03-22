package com.example.flynow.ui.screens.passengers.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowTextField
import com.example.flynow.utils.Constants

//component that creates the email text field
@Composable
fun EmailTextField(
    sharedViewModel: SharedViewModel,
    onEmailChange: (String) -> Unit,
    buttonClicked: Boolean,
    index: Int
) {
    FlyNowTextField(
        text = sharedViewModel.passengers[index].email.value,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        label = buildAnnotatedString {
            append("Email")
            withStyle(Constants.superscript) {
                append("*")
            }
        }.toString(),
        readOnly = false,
        onTextChange = { onEmailChange(it) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email),
        isError = (sharedViewModel.passengers[index].email.value == "" && buttonClicked)
    )
}