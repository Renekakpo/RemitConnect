package com.example.remitconnect.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.remitconnect.ui.theme.RemitConnectTheme

@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier,
    colors: ButtonColors,
    shape: Shape,
    enabled: Boolean,
    text: String,
    textColor: Color
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        shape = shape,
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(vertical = 6.dp)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun CustomButtonPreview() {
    RemitConnectTheme {
        Row {
            CustomButton(
                onClick = { /* Handle click */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.small,
                enabled = true,
                text = "Previous recipients",
                textColor =  MaterialTheme.colorScheme.background
            )
        }
    }
}