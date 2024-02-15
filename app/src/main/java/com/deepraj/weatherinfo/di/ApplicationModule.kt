package com.deepraj.weatherinfo.di

import com.deepraj.weatherinfo.api.ApiService
import com.deepraj.weatherinfo.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    @Singleton
    fun provideRetrofitInstance(): ApiService =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
}