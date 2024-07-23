package com.example.remitconnect.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.remitconnect.R
import com.example.remitconnect.ui.theme.RemitConnectTheme
import com.example.remitconnect.utils.NanpVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomInputField(
    value: String = "",
    onValueChange: (String) -> Unit = {},
    placeholderText: String?,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        containerColor = Color.LightGray,
        unfocusedBorderColor = Color.Transparent,
        focusedBorderColor = Color.Transparent
    ),
    shape: Shape?,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            if (!placeholderText.isNullOrEmpty()) {
                Text(placeholderText, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = colors,
        shape = shape ?: MaterialTheme.shapes.large,
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        enabled = enabled
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CustomInputFieldPreview() {
    val keyboardController = LocalSoftwareKeyboardController.current

    RemitConnectTheme {
        CustomInputField(
            value = "",
            onValueChange = {},
            placeholderText = null, // Optinal placeholder
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White, // White container color
                unfocusedBorderColor = Color.LightGray, // Light gray border
                focusedBorderColor = Color.LightGray // Light gray border
            ),
            shape = MaterialTheme.shapes.medium,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_search_24),
                    contentDescription = "Search Icon",
                    tint = Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Close the keyboard when the Done action is clicked
                    keyboardController?.hide()
                }
            )
        )
    }
}