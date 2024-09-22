package com.example.weatherapp.data.network

import com.example.weatherapp.domain.models.geo.CoordinatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocoderApi {
    @GET("/maps/api/geocode/json")
    suspend fun getGeoData(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): Response<CoordinatesResponse>
}