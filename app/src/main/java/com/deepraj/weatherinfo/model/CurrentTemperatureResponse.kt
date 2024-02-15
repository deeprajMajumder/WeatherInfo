package com.deepraj.weatherinfo.model

import com.google.gson.annotations.SerializedName

data class CurrentTemperatureResponse(
    @SerializedName("name")
    val cityName: String,
    @SerializedName("main")
    val currentTemperatureResponse: CurrentTemperatureResponseMain
)
