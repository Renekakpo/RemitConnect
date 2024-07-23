package com.example.remitconnect.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.remitconnect.data.local.entities.typeConverters.RecipientConverter
import com.example.remitconnect.data.model.Recipient
import com.example.remitconnect.utils.Constant.TRANSACTION_TABLE_NAME
import kotlinx.serialization.Serializable

@Entity(tableName = TRANSACTION_TABLE_NAME, indices = [Index(value = ["id"], unique = true)])
@Serializable
data class Transaction(
    @PrimaryKey(autoGenerate = true) // Automatically generates a unique ID for each transaction
    val id: Int = -1, // Unique identifier for the transaction, default is -1 indicating a new entry
    val option: String? = null, // Optional field for additional options related to the transaction
    val from: String? = null, // Optional field for specifying the source of the transaction
    @TypeConverters(RecipientConverter::class) // Specifies a custom type converter for the 'recipient' field
    val recipient: Recipient? = null, // Optional field for storing recipient details
    val selectedWallet: String? = null, // Optional field for storing the selected wallet for the transaction
    val currencyCode: String = "EUR", // Currency code for the transaction, default is Euro (EUR)
    val via: String? = null, // Optional field for specifying the method or channel of the transaction
    val amount: String? = null, // Optional field for storing the amount of the transaction
    val monecoFees: Double = 1.5, // Fee applied by Moneco, default is 1.5 units
    val transferFees: Double = 0.05, // Fee applied for the transfer, default is 0.05 units
    val conversionRate: Double = 655.94, // Conversion rate used for the transaction, default is 655.94
    val totalSpent: Double = 0.0, // Total amount spent in the transaction, default is 0.0
    val amountReceived: Double = 0.0, // Amount received in the transaction, default is 0.0
    val date: Long = System.currentTimeMillis() // Timestamp of when the transaction was created, default is the current time
)
