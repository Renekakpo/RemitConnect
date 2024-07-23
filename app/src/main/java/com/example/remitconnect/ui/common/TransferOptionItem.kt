package com.example.remitconnect.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.remitconnect.R
import com.example.remitconnect.data.model.MoneyTransferOpt

@Composable
fun TransferOptionItem(item: MoneyTransferOpt, onClicked: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClicked)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomIcon(
            imageResId = item.startIconId,
            contentDescription = stringResource(R.string.close_button_icon_desc),
            size = 34.dp,
            shape = RoundedCornerShape(10.dp),
            backgroundColor = MaterialTheme.colorScheme.surface
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(item.vector, contentDescription = stringResource(R.string.left_arrow_icon_desc))
    }
}