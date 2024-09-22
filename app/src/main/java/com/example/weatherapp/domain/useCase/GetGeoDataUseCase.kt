package com.example.weatherapp.domain.useCase

import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.domain.models.geo.CoordinatesResponse
import javax.inject.Inject

class GetGeoDataUseCase @Inject constructor(private val weatherRepository: WeatherRepositoryImpl) {

    suspend fun execute(searchString: String): CoordinatesResponse {
        return weatherRepository.getGeoData(searchString)
    }
}