package com.example.weatherapp.data.repository

import com.example.weatherapp.data.network.GeocoderApi
import com.example.weatherapp.data.network.NetworkApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

//I had not enough time to write unit tests for MainViewModel or Espresso UI tests
//So I did few tests for WeatherRepository, to show usage of Mockito and JUnit
@RunWith(MockitoJUnitRunner::class)
class WeatherRepositoryImplTest {

    private val searchString = "someCity"
    private val testLat = ""
    private val testLon = ""
    private lateinit var weatherRepository: WeatherRepositoryImpl

    @Mock
    lateinit var networkApiMock: NetworkApi
    @Mock
    lateinit var geoApiMock: GeocoderApi

    private val scope = CoroutineScope(Dispatchers.IO)

    @Before
    fun setUp() {
        weatherRepository = WeatherRepositoryImpl(networkApiMock, geoApiMock)
    }

    @Test
    fun getGeoData() {
        scope.launch {
            weatherRepository.getGeoData(searchString)
            verify(geoApiMock, times(1)).getGeoData(searchString, any())
        }
    }

    @Test
    fun getGeoWeatherDataByCoordinates() {
        scope.launch {
            weatherRepository.getGeoWeatherDataByCoordinates(testLat, testLon)
            verify(networkApiMock, times(1)).getWeatherDataByCoordinates(testLat, testLon, any())
        }
    }
}