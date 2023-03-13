package com.raju.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.raju.weatherapp.databinding.ActivityMainBinding
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), WeatherResultCallBack {
    var TODO: String = "Weather report"
    var apiKey = "21a68cae4a5283322eb02080681a7bd5"
    private lateinit var locationManager: LocationManager
    private val PERMISSION_CODE: Int=1
    lateinit var  binding: ActivityMainBinding
    lateinit var viewModel: WeatheViewModel
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        binding.viewmodel = ViewModelProvider(this,WeatherViewModelFactory(this))[WeatheViewModel::class.java]
        viewModel= ViewModelProvider(this)[WeatheViewModel::class.java]

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // check the location permission
        if (ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            //ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),PERMISSION_CODE)
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION), 1);
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION), 1);
            }
        }
       //  get the user last location
        var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        var cityName =  getcityName(location!!.latitude, location.longitude)
        Log.e(TODO,"Last Location: $cityName")
        binding.textViewTitle.text=cityName

        viewModel.getUserWeather().observe(this, androidx.lifecycle.Observer {
           var temp = it!!.temp - 273.15
            binding.textViewTemp.text =  temp.toString().substring(0,2)+ " °C"
            binding.textViewHumidity.text = it.humidity.toString() + " %"
            binding.textViewWind.text = it.wind.toString() + "  m/s"
            //binding.textView.text = it.wind.toString()
            binding.textViewTitle.text=it.name
            binding.textViewTitleTemp.text = temp.toString().substring(0,2)+ " °C" + "    "+it.description
          // Glide.with(this).load("http://openweathermap.org/img/w/" + it.icon + ".png").into(binding.imageView2)
           binding.textViewDate.text = Utilities.dateFormat( it.dt)
            binding.textViewSunrise.text= Utilities.timeFormat(it.sunrise)
            binding.textViewSunset.text= Utilities.timeFormat(it.sunset)
        })
    }


    private fun getcityName(latitude: Double, longitude: Double): String {
        var cityName = "Not found"
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses2 = geocoder.getFromLocation(latitude, longitude, 2)
        // For Android SDK < 33, the addresses list will be still obtained from the getFromLocation() method
        if (addresses2 != null) {
            for (addr: Address in addresses2){
                var city = addr.locality
                cityName=city
                viewModel.getWeatherReport(cityName,apiKey)
            }
        }
        return cityName
    }


    override fun onRequestPermissionsResult( requestCode: Int,permissions: Array<out String>,grantResults: IntArray ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
          if (requestCode== PERMISSION_CODE){
              if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                  Toast.makeText(this,"PERMISSION GRANTED",Toast.LENGTH_LONG).show()
              }else{
                  Toast.makeText(this,"PERMISSION NOT GRANTED",Toast.LENGTH_LONG).show()
              }
          }
    }

    override fun onSuccess(cityName: String) {
        Toast.makeText(applicationContext,cityName,Toast.LENGTH_LONG).show()
        viewModel.getWeatherReport(cityName,apiKey)
    }

    override fun onError(message: String) {
        Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
    }
}