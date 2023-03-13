package com.raju.weatherapp

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raju.weatherapp.apirequest.ApiRequest
import com.raju.weatherapp.apirequest.RetrofitRequest
import com.raju.weatherapp.model.UserWeather
import com.raju.weatherapp.model.Weather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatheViewModel(private var listsener: WeatherResultCallBack) : ViewModel() {
     val weatherField: WeatherField
     var weatherList = MutableLiveData<UserWeather?>()
     init {
         weatherField = WeatherField("")
     }
    val fieldTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                weatherField.setCityNmae(s.toString().trim())
            }
        }
    fun onLoginClicked(view: View){
        var loginCode:Int =weatherField.isDataValid()
        if (loginCode == 0){
            listsener.onError("field not be null")
        }
        else
            listsener.onSuccess(weatherField.getCityName())
    }
    fun getWeatherReport( cityName: String,apiKey: String){

        CoroutineScope(IO).launch {
            val retrofitInstsnce= RetrofitRequest.getRetroInstance().create(ApiRequest::class.java)
            var call= retrofitInstsnce.getDataFromApi(cityName,apiKey)
            call.enqueue(object : Callback<Weather> {
                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    Log.e("ViewModel","viewmodel: ${response.body()}")
                    if (response.isSuccessful && response.body()!=null) {

                        val userWeather = UserWeather(
                            response.body()!!.name,
                            response.body()!!.main.temp,
                            response.body()!!.main.humidity,
                            response.body()!!.main.pressure,
                            response.body()!!.wind.speed,
                            response.body()!!.visibility,
                            response.body()!!.weather[0].description,
                            response.body()!!.weather[0].icon,
                            response.body()!!.dt,
                            response.body()!!.sys.sunrise,
                            response.body()!!.sys.sunset
                        )
                        weatherList.postValue(userWeather)
                    }
                    /*val unix_seconds: Int? = response.body()?.timezone
                    val date = Date( (Long)unix_seconds * 1000L)
                    // format of the date
                    // format of the date
                    val formate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
                    formate.setTimeZone(TimeZone.getTimeZone("GMT+5:30"))
                    val dateFinal: String = formate.format(date)
                    Log.e("ViewModel","date: $dateFinal")*/

                }

                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    Log.e("viewmodel","Error: ${t.message}")
                    weatherList.postValue(null)
                }

            })
        }

    }

    fun getUserWeather(): MutableLiveData<UserWeather?> {
        return weatherList
    }

}