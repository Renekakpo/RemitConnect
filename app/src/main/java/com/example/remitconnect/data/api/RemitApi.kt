package com.example.remitconnect.data.api

import com.example.remitconnect.data.model.MobileWallet
import com.example.remitconnect.data.model.Recipient
import retrofit2.http.GET
import retrofit2.http.Headers

// Define an interface to interact with the Remit API
interface RemitApi {

    // Define a method to get a list of mobile wallets
    @Headers("Content-Type: application/json")  // Set the content type to JSON for the request
    @GET("wallets")  // Specify the endpoint for fetching mobile wallets
    suspend fun getMobileWallets(): List<MobileWallet>  // Suspend function to perform the network request asynchronously

    // Define a method to get a list of previous recipients
    @Headers("Content-Type: application/json")  // Set the content type to JSON for the request
    @GET("recipients")  // Specify the endpoint for fetching previous recipients
    suspend fun getPreviousRecipients(): List<Recipient>  // Suspend function to perform the network request asynchronously
}