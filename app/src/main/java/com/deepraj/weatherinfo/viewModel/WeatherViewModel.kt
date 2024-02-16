package com.deepraj.weatherinfo.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepraj.weatherinfo.respository.NetworkRepository
import com.deepraj.weatherinfo.utils.Converters
import com.deepraj.weatherinfo.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val TAG = WeatherViewModel::class.java.name
    private val _currentTemp = MutableLiveData<Int>()
    val currentTemp: LiveData<Int>
        get() = _currentTemp

    private val _cityName = MutableLiveData<String>()
    val cityName: LiveData<String>
        get() = _cityName

    private val _forecastList = MutableLiveData<List<ForecastItemViewModel>>()
    val forecastList: LiveData<List<ForecastItemViewModel>>
        get() = _forecastList

    private val _isLoaded = MutableLiveData<Boolean>(false)
    val isLoaded: LiveData<Boolean>
        get() = _isLoaded

    private val _isFailure = MutableLiveData<Boolean>(false)
    val isFailure: LiveData<Boolean>
        get() = _isFailure

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        _isFailure.value = true
        Log.d(TAG,"exceptionHandler _isFailure: ${_isFailure.value}")
    }

    init {
        fetchCurrentTempAndForecast()
    }
    fun resetFailure() {
        _isFailure.value = false
    }

     fun fetchCurrentTempAndForecast() {
        viewModelScope.launch(exceptionHandler) {
            if (networkHelper.isNetworkConnected()) {
                fetchTodayWeather(this)
                fetchForecast(this)
                _isLoaded.value = true
            } else {
                _isFailure.value = true
            }
        }
    }
    private suspend fun fetchForecast(coroutineScope: CoroutineScope) {
        networkRepository.getForecast("Bengaluru").let { response ->
            Log.d(TAG,"response: ${response.body().toString()} ")
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    val forecastTempList = ArrayList<ForecastItemViewModel>()
                    val today = computeDayString(data.forecastList[0].dateAndTime)
                    var tomorrow = ""
                    for (forecast in data.forecastList) {
                        if (computeDayString(forecast.dateAndTime) != today) {
                            tomorrow = computeDayString(forecast.dateAndTime)
                            break
                        }
                    }
                    var countOfDay = 0
                    var sumOfAllTemp = 0.0
                    data.forecastList.forEach { forecast ->
                        if (computeDayString(forecast.dateAndTime) != today) {
                            if (computeDayString(forecast.dateAndTime) == tomorrow) {
                                sumOfAllTemp += forecast.currentTemperatureMain.temperature
                                countOfDay++
                            } else {
                                val forecastItem =
                                    ForecastItemViewModel(
                                        sumOfAllTemp / countOfDay,
                                        tomorrow
                                    )
                                tomorrow = computeDayString(forecast.dateAndTime)
                                sumOfAllTemp = 0.0
                                countOfDay = 0
                                forecastTempList.add(forecastItem)
                            }
                        }
                    }
                    _forecastList.value = forecastTempList
                }
            } else {
                coroutineScope.cancel()
                _isFailure.value = true
            }
        }
    }

    private suspend fun fetchTodayWeather(coroutineScope: CoroutineScope) {
        networkRepository.getCurrentTemperature("Bengaluru").let { response ->
            if (response.isSuccessful) {
                Log.d(TAG,"response: ${response.body().toString()} ")
                response.body()?.let { data ->
                    val temperatureInCelsius =
                        Converters.kelvinToCelsius(data.currentTemperatureResponse.temperature)
                    _currentTemp.value = temperatureInCelsius
                    _cityName.value = data.cityName
                }
            } else {
                _isFailure.value = true
                coroutineScope.cancel()
            }
        }
    }

    private fun computeDayString(dateAndTime: String): String {
        val timestamp = dateAndTime.toLong() * 1000 // Convert seconds to milliseconds since unix time is sent in response
        val date = Date(timestamp)
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
    }
}