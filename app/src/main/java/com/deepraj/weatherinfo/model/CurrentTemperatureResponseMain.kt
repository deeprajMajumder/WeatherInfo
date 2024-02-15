package com.deepraj.weatherinfo.model

import com.google.gson.annotations.SerializedName

data class CurrentTemperatureResponseMain(
    @SerializedName("temp")
    val temperature: Double,
)
