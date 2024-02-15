package com.deepraj.weatherinfo.respository

import com.deepraj.weatherinfo.api.ApiService
import com.deepraj.weatherinfo.model.CurrentTemperatureResponse
import com.deepraj.weatherinfo.model.ForecastResponse
import retrofit2.Response
import javax.inject.Inject

class NetworkRepository @Inject constructor(private val networkService: ApiService) {

    suspend fun getCurrentTemperature(cityName: String): Response<CurrentTemperatureResponse> {
        return networkService.getCurrentTemperature(cityName)
    }

    suspend fun getForecast(cityName: String): Response<ForecastResponse> {
        return networkService.getForecast(cityName)
    }
}