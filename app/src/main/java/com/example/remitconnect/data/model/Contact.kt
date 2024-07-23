package com.example.remitconnect.data.model

// Represents a contact with essential details
data class Contact(
    // The contact's first name
    val firstName: String,

    // The contact's last name
    val lastName: String,

    // The contact's phone number
    val phone: String,

    // The resource ID for the contact's image (usually a drawable resource)
    val image: Int
)


