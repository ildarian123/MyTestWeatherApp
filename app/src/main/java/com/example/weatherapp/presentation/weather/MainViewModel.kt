package com.example.weatherapp.presentation.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.domain.models.geo.CoordinatesResponse
import com.example.weatherapp.domain.models.weather.WeatherResponse
import com.example.weatherapp.domain.useCase.GetGeoDataUseCase
import com.example.weatherapp.domain.useCase.GetWeatherDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val getGeoDataUseCase: GetGeoDataUseCase
) :
    ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    var weatherData: MutableLiveData<WeatherResponse> = MutableLiveData()
    var geoData: MutableLiveData<CoordinatesResponse> = MutableLiveData()
    var locationPermissionGranted: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getWeatherData(lat: String, lon: String) {
        scope.launch {
            weatherData.postValue(getWeatherDataUseCase.execute(lat, lon))
        }
    }

    fun getGeoData(searchString: String) {
        scope.launch {
            geoData.postValue(getGeoDataUseCase.execute(searchString))
        }
    }

}