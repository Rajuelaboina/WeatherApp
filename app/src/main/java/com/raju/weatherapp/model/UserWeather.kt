package com.raju.weatherapp.model

data class UserWeather(
    val name: String,
    var temp: Double,
    var humidity: Int,
    var param: Any,
    var wind: Double,
    var visibility: Int?,
    val description: String,
    val icon: String,
    val dt: Int,
    val sunrise: Int,
    val sunset: Int
) {
}