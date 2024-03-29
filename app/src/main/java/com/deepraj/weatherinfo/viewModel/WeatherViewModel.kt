package com.deepraj.weatherinfo.viewModel

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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

    private val _cityName = MutableLiveData<String>().apply {
        value = "Bengaluru" //Default city
    }
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
    fun updateCityName(cityName: String, actionId: Int, view : View) : Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            _cityName.value = cityName
            fetchCurrentTempAndForecast()
            // Hide the keyboard
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
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
        networkRepository.getForecast(cityName.value!!).let { response ->
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    val forecastTempList = ArrayList<ForecastItemViewModel>()
                    val today = computeDayString(data.forecastList[0].dateAndTime)
                    var tomorrow = ""
                    var sumOfAllTemp = 0.0
                    var countOfDay = 0

                    data.forecastList.forEach { forecast ->
                        val forecastDay = computeDayString(forecast.dateAndTime)
                        if (forecastDay != today) {
                            if (tomorrow.isEmpty()) {
                                tomorrow = forecastDay
                            }
                            if (forecastDay == tomorrow) {
                                sumOfAllTemp += forecast.currentTemperatureMain.temperature
                                countOfDay++
                            } else {
                                forecastTempList.add(ForecastItemViewModel(sumOfAllTemp / countOfDay, tomorrow))
                                tomorrow = forecastDay
                                sumOfAllTemp = forecast.currentTemperatureMain.temperature
                                countOfDay = 1
                            }
                        }
                    }

                    // Add the last computed forecast item
                    if (countOfDay > 0) {
                        forecastTempList.add(ForecastItemViewModel(sumOfAllTemp / countOfDay, tomorrow))
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
        networkRepository.getCurrentTemperature(cityName.value!!).let { response ->
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