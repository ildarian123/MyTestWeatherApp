package com.example.weatherapp.data.repository

import com.example.weatherapp.data.network.GeocoderApi
import com.example.weatherapp.data.network.NetworkApi
import com.example.weatherapp.domain.models.geo.CoordinatesResponse
import com.example.weatherapp.domain.models.weather.WeatherResponse
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val networkApi: NetworkApi,
    private val geocoderApi: GeocoderApi
) : WeatherRepository {

    companion object {
        private const val WEATHER_API_KEY = "aca39e900dd504c95bcaf4f448cb9917"
        private const val GEO_API_KEY = "AIzaSyCKfUh0E39L650be9j_AlJU1k4ugQvSX8c"
    }

    override suspend fun getGeoData(searchString: String): CoordinatesResponse {
        val result = geocoderApi.getGeoData(searchString, GEO_API_KEY)
        return result.body() ?: CoordinatesResponse()
    }

    override suspend fun getGeoWeatherDataByCoordinates(
        lat: String,
        lon: String
    ): WeatherResponse {
        val result = networkApi.getWeatherDataByCoordinates(lat, lon, WEATHER_API_KEY)
        return result.body() ?: WeatherResponse()
    }

}