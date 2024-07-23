package com.example.remitconnect.data.model

data class Country(
    // Name of the country
    val name: String,

    // ISO 3166-1 alpha-2 country code (e.g., "US" for United States)
    val code: String,

    // International dialing prefix (e.g., "+1" for the United States)
    val phonePrefix: String,

    // URL or path to the country's flag image
    val flag: String
)
