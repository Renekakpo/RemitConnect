package com.example.remitconnect.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Recipient(
    // Unique identifier for the recipient
    val id: String,

    // Name of the recipient
    val name: String,

    // Country where the recipient is located
    val country: String,

    // Mobile wallet identifier (e.g., phone number or wallet ID) with custom serialization name
    @SerializedName("mobile_wallet")
    val mobileWallet: String,

    // Optional phone number of the recipient
    val phoneNumber: String?,

    // Optional currency code (e.g., USD, EUR) for the recipient's transactions
    val currencyCode: String?
)