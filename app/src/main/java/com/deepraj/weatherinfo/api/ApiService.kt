package com.deepraj.weatherinfo.api

import com.deepraj.weatherinfo.model.CurrentTemperatureResponse
import com.deepraj.weatherinfo.model.ForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(ApiEndpoints.WEATHER)
    suspend fun getCurrentTemperature(
        @Query("q") cityName: String,
        @Query("APPID") apiKey: String = "b36e7fc76df2e3bb3a87c61f8120a7eb"
    ): Response<CurrentTemperatureResponse>

    @GET(ApiEndpoints.FORECAST)
    suspend fun getForecast(
        @Query("q") cityName: String,
        @Query("APPID") apiKey: String = "b36e7fc76df2e3bb3a87c61f8120a7eb"
    ): Response<ForecastResponse>
}