package com.example.weatherapp.data.repository

import com.example.weatherapp.domain.models.geo.CoordinatesResponse
import com.example.weatherapp.domain.models.weather.WeatherResponse

interface WeatherRepository {

    suspend fun getGeoData(searchString: String): CoordinatesResponse
    suspend fun getGeoWeatherDataByCoordinates(lat: String, lon: String): WeatherResponse

}