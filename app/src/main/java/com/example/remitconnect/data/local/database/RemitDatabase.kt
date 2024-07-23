package com.example.remitconnect.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.remitconnect.data.local.dao.TransactionDao
import com.example.remitconnect.data.local.entities.Transaction
import com.example.remitconnect.data.local.entities.typeConverters.RecipientConverter

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(RecipientConverter::class)
abstract class RemitDatabase: RoomDatabase() {
    abstract fun transactionDAO(): TransactionDao
}