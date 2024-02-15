package com.deepraj.weatherinfo.utils

import kotlin.math.roundToInt

object Converters {
  fun kelvinToCelsius(kelvin: Double): Int {
    return (kelvin + Constants.ABSOLUTE_ZERO_IN_CELSIUS).roundToInt()
  }
}
