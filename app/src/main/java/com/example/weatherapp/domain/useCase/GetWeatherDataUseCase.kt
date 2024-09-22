package com.example.weatherapp.domain.useCase

import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.domain.models.weather.WeatherResponse
import javax.inject.Inject

class GetWeatherDataUseCase @Inject constructor(private val weatherRepository: WeatherRepositoryImpl) {

    suspend fun execute(lat: String, lon: String): WeatherResponse {
        return weatherRepository.getGeoWeatherDataByCoordinates(lat, lon)
    }
}