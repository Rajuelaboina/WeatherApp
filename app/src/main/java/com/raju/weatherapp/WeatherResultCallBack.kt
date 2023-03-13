package com.raju.weatherapp

interface WeatherResultCallBack {
    fun onSuccess(message: String)
    fun onError(message: String)
}