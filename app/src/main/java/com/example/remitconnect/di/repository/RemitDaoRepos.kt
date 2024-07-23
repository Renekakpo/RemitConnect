package com.example.remitconnect.di.repository

import com.example.remitconnect.data.local.dao.TransactionDao
import com.example.remitconnect.data.local.entities.Transaction
import javax.inject.Inject

class RemitDaoRepos @Inject constructor(private val transactionDAO: TransactionDao) {
    suspend fun insertTransaction(transaction: Transaction) = transactionDAO.insertTransaction(transaction)

    suspend fun getLast5Transactions() = transactionDAO.getLast5Transactions()

    suspend fun getTotalSpentSum() = transactionDAO.getTotalSpentSum()
}