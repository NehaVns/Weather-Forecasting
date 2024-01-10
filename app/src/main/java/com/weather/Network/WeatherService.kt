package com.weather.Network

import com.weatherapp.models.WeatherResponse
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Query

interface WeatherService {

    @GET("2.5/weather")
    fun getweather(
        @Query("lat") lat: Double,
        @Query("lon") laon: Double,
        @Query("units") units: String?,
        @Query("appid") appid: String?,
    ) : Call<WeatherResponse>
}