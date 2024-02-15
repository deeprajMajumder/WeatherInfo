package com.deepraj.weatherinfo.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("list")
    val forecastList: List<IndividualForecastResponse>
)
