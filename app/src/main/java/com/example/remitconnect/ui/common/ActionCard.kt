package com.example.remitconnect.ui.common

import android.media.session.PlaybackState.CustomAction
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.remitconnect.R
import com.example.remitconnect.ui.theme.RemitConnectTheme

@Composable
fun ActionCard(
    width: Dp,
    height: Dp,
    imageResId: Int,
    imageResDescription: String,
    cardTitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(width)
            .height(height),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.onSecondary,
        onClick = onClick) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            CustomIcon(
                imageResId = imageResId,
                contentDescription = imageResDescription,
                size = 40.dp,
                shape = RoundedCornerShape(10.dp),
                backgroundColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                cardTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionCardPreview() {
    RemitConnectTheme {
        ActionCard(
            width = 150.dp,
            height = 100.dp,
            imageResId = R.drawable.ic_moneys,
            imageResDescription = "Money Icon",
            cardTitle = "Send Money",
            onClick = {}
        )
    }
}