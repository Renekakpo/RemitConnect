package com.example.remitconnect.di.module

import com.example.remitconnect.data.local.dao.TransactionDao
import com.example.remitconnect.data.local.database.RemitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TransactionDaoModule {

    @Singleton
    @Provides
    fun provideTransactionDao(database: RemitDatabase): TransactionDao {
        return database.transactionDAO()
    }
}