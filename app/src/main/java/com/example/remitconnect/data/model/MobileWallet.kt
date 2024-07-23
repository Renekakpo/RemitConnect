package com.example.remitconnect.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing a mobile wallet.
 *
 * @property id Unique identifier for the mobile wallet.
 * @property name Name of the mobile wallet.
 * @property image Optional integer resource ID for the wallet's image. It can be null if no image is provided.
 */
@Serializable
data class MobileWallet(
    val id: String,            // Unique identifier for the mobile wallet
    val name: String,          // Name of the mobile wallet
    var image: Int? = null     // Optional resource ID for the wallet's image, default is null
)

