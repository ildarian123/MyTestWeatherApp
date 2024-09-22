package com.example.weatherapp.data.network

import com.example.weatherapp.domain.models.weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApi {

    @GET("/data/2.5/weather/")
    suspend fun getWeatherDataByCoordinates(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String
    ): Response<WeatherResponse>

}