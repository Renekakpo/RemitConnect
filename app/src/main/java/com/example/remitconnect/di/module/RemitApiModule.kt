package com.example.remitconnect.di.module

import com.example.remitconnect.data.api.RemitApi
import com.example.remitconnect.di.repository.RemitApiRepository
import com.example.remitconnect.utils.Constant.REMIT_API_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemitApiModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(REMIT_API_BASE_URL).build()
    }

    @Singleton
    @Provides
    fun provideRemitApi(retrofit: Retrofit): RemitApi {
        return retrofit.create(RemitApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRemitApiRepository(remitApi: RemitApi): RemitApiRepository {
        return RemitApiRepository(remitApi)
    }
}