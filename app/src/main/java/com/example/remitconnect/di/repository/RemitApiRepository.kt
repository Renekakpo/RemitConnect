package com.example.remitconnect.di.repository

import com.example.remitconnect.data.api.RemitApi
import com.example.remitconnect.data.model.MobileWallet
import com.example.remitconnect.data.model.Recipient
import javax.inject.Inject

class RemitApiRepository @Inject constructor(private val api: RemitApi) {
    suspend fun getMobileWallets(): List<MobileWallet> = api.getMobileWallets()

    suspend fun getRecipients(): List<Recipient> = api.getPreviousRecipients()
}