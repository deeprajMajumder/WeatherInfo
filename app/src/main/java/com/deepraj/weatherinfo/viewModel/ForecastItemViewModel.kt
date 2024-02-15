package com.deepraj.weatherinfo.viewModel

import com.deepraj.weatherinfo.utils.Converters
import androidx.lifecycle.ViewModel


class ForecastItemViewModel(
  private val averageTemp: Double,
  val day: String
) : ViewModel() {

  val temperature: Int by lazy {
    setTemperature()
  }

  private fun setTemperature(): Int {
    return Converters.kelvinToCelsius(averageTemp)
  }
}