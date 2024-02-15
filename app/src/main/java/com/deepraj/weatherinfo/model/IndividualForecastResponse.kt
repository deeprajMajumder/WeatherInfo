package com.deepraj.weatherinfo.model

import com.google.gson.annotations.SerializedName

data class IndividualForecastResponse(
    @SerializedName("main")
    val currentTemperatureMain: CurrentTemperatureResponseMain,
    @SerializedName("dt")
    val dateAndTime: String
)
