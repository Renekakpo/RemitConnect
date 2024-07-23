package com.example.remitconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.remitconnect.data.local.entities.Transaction
import com.example.remitconnect.utils.Constant.TRANSACTION_TABLE_NAME

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Query("SELECT * FROM $TRANSACTION_TABLE_NAME ORDER BY id DESC LIMIT 5")
    suspend fun getLast5Transactions(): List<Transaction>

    @Query("SELECT SUM(totalSpent) FROM $TRANSACTION_TABLE_NAME")
    suspend fun getTotalSpentSum(): Double?
}