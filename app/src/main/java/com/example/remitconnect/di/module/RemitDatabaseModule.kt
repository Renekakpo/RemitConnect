package com.example.remitconnect.di.module

import android.app.Application
import androidx.room.Room
import com.example.remitconnect.data.local.database.RemitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemitDatabaseModule {

    @Singleton
    @Provides
    fun provideRemitDatabase(application: Application): RemitDatabase {
        return Room.databaseBuilder(application, RemitDatabase::class.java, "remit_connect_db")
            .build()
    }
}