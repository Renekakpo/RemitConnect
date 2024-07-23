package com.example.remitconnect.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.remitconnect.R

// Data class representing an option for a money transfer.
// It includes an icon, a title, and an optional vector graphic for the right arrow icon.
data class MoneyTransferOpt(
    // Resource ID for the start icon to be displayed alongside the option.
    val startIconId: Int,

    // Title text for the money transfer option.
    val title: String,

    // Vector graphic to be used for the right arrow icon; defaults to the keyboard arrow right icon.
    // This can be overridden if a different icon is needed.
    val vector: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
)

val sampleMoneyTransferOptList = listOf(
    MoneyTransferOpt(R.drawable.ic_user_square, "To Moneco balance"),
    MoneyTransferOpt(R.drawable.ic_store, "Bank transfer"),
    MoneyTransferOpt(R.drawable.ic_world, "Send to Africa"),
)

val sampleTransferDestOptList = listOf(
    MoneyTransferOpt(R.drawable.ic_arrow_square_right, "Mobile wallets"),
    MoneyTransferOpt(R.drawable.ic_arrow_square_right, "Bank transfer"),
)
