package com.example.remitconnect.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.remitconnect.R
import com.example.remitconnect.ui.theme.RemitConnectTheme

@Composable
fun CustomIcon(
    imageResId: Int,
    contentDescription: String,
    size: Dp,
    shape: RoundedCornerShape,
    backgroundColor: Color,
    onClicked: () -> Unit = {},
    colorFilter: ColorFilter? = null
) {
    Surface(
        modifier = Modifier
            .size(size),
        shape = shape,
        color = backgroundColor,
        onClick = onClicked
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = contentDescription,
            modifier = Modifier.padding(9.dp),
            colorFilter = colorFilter
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomIconPreview() {
    RemitConnectTheme {
        CustomIcon(
            imageResId = R.drawable.ic_moneys,
            contentDescription = "Money Icon",
            size = 30.dp,
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color.LightGray,
            colorFilter = null
        )
    }
}